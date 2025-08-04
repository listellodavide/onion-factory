package com.execodex.demolocalai.service

import com.execodex.demolocalai.entities.Order
import com.execodex.demolocalai.pojos.StripeWebhookRequest
import com.execodex.demolocalai.repositories.ProductRepository
import com.stripe.Stripe
import com.stripe.model.Event
import com.stripe.model.EventDataObjectDeserializer
import com.stripe.model.PaymentIntent
import com.stripe.model.checkout.Session
import com.stripe.param.PaymentIntentCreateParams
import com.stripe.param.checkout.SessionCreateParams
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.math.BigDecimal

/**
 * Service for handling Stripe payment operations.
 */
@Service
class StripeService(
    @Value("\${stripe.api.secretKey}") private val secretKey: String,
    private val orderService: OrderService,
    private val productRepository: ProductRepository
) {
    private val logger = LoggerFactory.getLogger(StripeService::class.java)
    
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
                orderService.getOrderItemsByOrderId(orderId)
                    .flatMap { orderItem ->
                        // Fetch product details for each order item
                        productRepository.findById(orderItem.productId)
                            .map { product -> Pair(orderItem, product) }
                    }
                    .collectList()
                    .flatMap { orderItemsWithProducts ->
                        if (orderItemsWithProducts.isEmpty()) {
                            return@flatMap Mono.error(IllegalArgumentException("No items found for order: $orderId"))
                        }

                        Mono.fromCallable {
                            val paramsBuilder = SessionCreateParams.builder()
                                .setMode(SessionCreateParams.Mode.PAYMENT)
                                .setSuccessUrl(successUrl)
                                .setCancelUrl(cancelUrl)
                                .putMetadata("orderId", order.id.toString())
                                .setPaymentIntentData(
                                    SessionCreateParams.PaymentIntentData.builder()
                                        .putMetadata("orderId", order.id.toString())
                                        .build()
                                )

                            // Add each order item as a line item
                            orderItemsWithProducts.forEach { (item, product) ->
                                // Convert BigDecimal to cents (long) for Stripe
                                val itemAmountInCents = item.price.multiply(BigDecimal(100)).toLong()

                                paramsBuilder.addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                        .setQuantity(item.quantity.toLong())
                                        .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount(itemAmountInCents)
                                                .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName(product.name)
                                                        .setDescription("Order #${order.id} - ${product.name}")
                                                        .build()
                                                )
                                                .build()
                                        )
                                        .build()
                                )
                            }

                            Session.create(paramsBuilder.build())
                        }
                    }
            }
    }
    
    /**
     * Handle Stripe webhook events.
     * This method processes various Stripe events like payment_intent.succeeded,
     * checkout.session.completed, etc.
     *
     * @param webhookRequest the webhook event data from Stripe
     * @return a Mono containing the processed order (if applicable)
     */
    fun handleWebhookEvent(webhookRequest: StripeWebhookRequest): Mono<Order?> {
        logger.info("Received Stripe webhook event: ${webhookRequest.type} with ID: ${webhookRequest.id}")
        logger.info("Webhook event data: ${webhookRequest.data.`object`}")
        return when (webhookRequest.type) {
            "checkout.session.completed" -> {
                // Handle successful checkout session
                val sessionId = webhookRequest.data.`object`.id
                val orderId = webhookRequest.data.`object`.metadata?.get("orderId")?.toLong()
                    ?: return Mono.error(IllegalStateException("Order ID not found in session metadata"))
                
                logger.info("Processing completed checkout session: $sessionId for order: $orderId")
                
                // Update order status to PAID
                orderService.getOrderById(orderId)
                    .switchIfEmpty(Mono.error(IllegalStateException("Order not found: $orderId")))
                    .flatMap { order ->
                        val updatedOrder = order.copy(status = "PAID")
                        orderService.updateOrder(orderId, updatedOrder)
                    }
            }
            
            "payment_intent.succeeded" -> {
                // Handle successful payment intent
                val paymentIntentId = webhookRequest.data.`object`.id
                val orderId = webhookRequest.data.`object`.metadata?.get("orderId")?.toLong()
                    ?: return Mono.error(IllegalStateException("Order ID not found in payment intent metadata"))
                
                logger.info("Processing succeeded payment intent: $paymentIntentId for order: $orderId")
                
                // Update order status to PAID
                orderService.getOrderById(orderId)
                    .switchIfEmpty(Mono.error(IllegalStateException("Order not found: $orderId")))
                    .flatMap { order ->
                        val updatedOrder = order.copy(status = "PAID")
                        orderService.updateOrder(orderId, updatedOrder)
                    }
            }
            
            else -> {
                // Log other events but don't process them
                logger.info("Received unhandled Stripe event type: ${webhookRequest.type}")
                Mono.empty()
            }
        }
    }
}