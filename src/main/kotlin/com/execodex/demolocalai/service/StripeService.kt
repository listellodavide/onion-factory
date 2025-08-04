package com.execodex.demolocalai.service

import com.execodex.demolocalai.entities.Order
import com.stripe.Stripe
import com.stripe.model.PaymentIntent
import com.stripe.model.checkout.Session
import com.stripe.param.PaymentIntentCreateParams
import com.stripe.param.checkout.SessionCreateParams
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.math.BigDecimal
import jakarta.annotation.PostConstruct

/**
 * Service for handling Stripe payment operations.
 */
@Service
class StripeService(
    @Value("\${stripe.api.secretKey}") private val secretKey: String,
    private val orderService: OrderService
) {
    @PostConstruct
    fun init() {
        Stripe.apiKey = secretKey
    }

    /**
     * Create a payment intent for an order.
     *
     * @param orderId the ID of the order to create a payment for
     * @return a Mono containing the created PaymentIntent
     */
    fun createPaymentIntent(orderId: Long): Mono<PaymentIntent> {
        return orderService.getOrderById(orderId)
            .switchIfEmpty(Mono.error(IllegalArgumentException("Order not found: $orderId")))
            .flatMap { order ->
                Mono.fromCallable {
                    // Convert BigDecimal to cents (long) for Stripe
                    val amountInCents = order.totalAmount.multiply(BigDecimal(100)).toLong()
                    
                    val params = PaymentIntentCreateParams.builder()
                        .setAmount(amountInCents)
                        .setCurrency("usd")
                        .setDescription("Payment for Order #${order.id}")
                        .putMetadata("orderId", order.id.toString())
                        .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                        )
                        .build()
                    
                    PaymentIntent.create(params)
                }
            }
    }

    /**
     * Confirm a payment was successful and update the order status.
     *
     * @param paymentIntentId the ID of the payment intent
     * @return a Mono containing the updated order
     */
    fun confirmPayment(paymentIntentId: String): Mono<Order> {
        return Mono.fromCallable {
            val paymentIntent = PaymentIntent.retrieve(paymentIntentId)
            
            if (paymentIntent.status != "succeeded") {
                throw IllegalStateException("Payment not successful. Status: ${paymentIntent.status}")
            }
            
            val orderId = paymentIntent.metadata["orderId"]?.toLong() 
                ?: throw IllegalStateException("Order ID not found in payment metadata")
                
            orderId
        }.flatMap { orderId ->
            orderService.getOrderById(orderId)
                .switchIfEmpty(Mono.error(IllegalStateException("Order not found: $orderId")))
                .flatMap { order ->
                    val updatedOrder = order.copy(status = "PAID")
                    orderService.updateOrder(orderId, updatedOrder)
                }
        }
    }
    
    /**
     * Create a Checkout Session for an order.
     *
     * @param orderId the ID of the order to create a checkout session for
     * @param successUrl the URL to redirect to on successful payment
     * @param cancelUrl the URL to redirect to if payment is cancelled
     * @return a Mono containing the created Checkout Session
     */
    fun createCheckoutSession(orderId: Long, successUrl: String, cancelUrl: String): Mono<Session> {
        return orderService.getOrderById(orderId)
            .switchIfEmpty(Mono.error(IllegalArgumentException("Order not found: $orderId")))
            .flatMap { order ->
                Mono.fromCallable {
                    // Convert BigDecimal to cents (long) for Stripe
                    val amountInCents = order.totalAmount.multiply(BigDecimal(100)).toLong()
                    
                    val params = SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(successUrl)
                        .setCancelUrl(cancelUrl)
                        .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("usd")
                                        .setUnitAmount(amountInCents)
                                        .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName("Order #${order.id}")
                                                .setDescription("Payment for Order #${order.id}")
                                                .build()
                                        )
                                        .build()
                                )
                                .build()
                        )
                        .putMetadata("orderId", order.id.toString())
                        .build()
                    
                    Session.create(params)
                }
            }
    }
}