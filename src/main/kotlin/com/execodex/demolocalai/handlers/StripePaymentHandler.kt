package com.execodex.demolocalai.handlers

import com.execodex.demolocalai.pojos.CheckoutSessionResponse
import com.execodex.demolocalai.pojos.ConfirmPaymentRequest
import com.execodex.demolocalai.pojos.CreateCheckoutSessionRequest
import com.execodex.demolocalai.pojos.CreatePaymentIntentRequest
import com.execodex.demolocalai.pojos.PaymentConfirmationResponse
import com.execodex.demolocalai.pojos.PaymentIntentResponse
import com.execodex.demolocalai.pojos.StripeWebhookRequest
import com.execodex.demolocalai.pojos.WebhookResponse
import com.execodex.demolocalai.service.StripeService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

/**
 * Handler for Stripe payment-related HTTP requests.
 */
@Component
class StripePaymentHandler(private val stripeService: StripeService) {
    private val logger = LoggerFactory.getLogger(StripePaymentHandler::class.java)

    /**
     * Create a payment intent for an order.
     *
     * @param request the server request containing the order ID
     * @return a server response containing the payment intent details
     */
    fun createPaymentIntent(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono<CreatePaymentIntentRequest>()
            .flatMap { paymentRequest ->
                stripeService.createPaymentIntent(paymentRequest.orderId)
                    .map { paymentIntent ->
                        PaymentIntentResponse(
                            clientSecret = paymentIntent.clientSecret,
                            paymentIntentId = paymentIntent.id,
                            amount = paymentIntent.amount,
                            currency = paymentIntent.currency
                        )
                    }
            }
            .flatMap { response -> ServerResponse.ok().bodyValue(response) }
            .onErrorResume { error ->
                when (error) {
                    is IllegalArgumentException -> ServerResponse.badRequest().bodyValue(error.message ?: "Bad request")
                    else -> ServerResponse.status(500).bodyValue("Internal server error: ${error.message}")
                }
            }
    }

    /**
     * Confirm a payment was successful and update the order status.
     *
     * @param request the server request containing the payment intent ID
     * @return a server response containing the confirmation result
     */
    fun confirmPayment(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono<ConfirmPaymentRequest>()
            .flatMap { confirmRequest ->
                stripeService.confirmPayment(confirmRequest.paymentIntentId)
                    .map { order ->
                        PaymentConfirmationResponse(
                            orderId = order.id!!,
                            status = order.status,
                            success = true,
                            message = "Payment confirmed successfully"
                        )
                    }
            }
            .flatMap { response -> ServerResponse.ok().bodyValue(response) }
            .onErrorResume { error ->
                val errorResponse = PaymentConfirmationResponse(
                    orderId = -1,
                    status = "FAILED",
                    success = false,
                    message = error.message ?: "Payment confirmation failed"
                )
                
                when (error) {
                    is IllegalArgumentException, is IllegalStateException -> 
                        ServerResponse.badRequest().bodyValue(errorResponse)
                    else -> 
                        ServerResponse.status(500).bodyValue(errorResponse)
                }
            }
    }
    
    /**
     * Create a checkout session for an order.
     *
     * @param request the server request containing the order ID and redirect URLs
     * @return a server response containing the checkout session details
     */
    fun createCheckoutSession(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono<CreateCheckoutSessionRequest>()
            .flatMap { checkoutRequest ->
                stripeService.createCheckoutSession(
                    checkoutRequest.orderId,
                    checkoutRequest.successUrl,
                    checkoutRequest.cancelUrl
                )
                .map { session ->
                    CheckoutSessionResponse(
                        sessionId = session.id,
                        checkoutUrl = session.url
                    )
                }
            }
            .flatMap { response -> ServerResponse.ok().bodyValue(response) }
            .onErrorResume { error ->
                when (error) {
                    is IllegalArgumentException -> ServerResponse.badRequest().bodyValue(error.message ?: "Bad request")
                    else -> ServerResponse.status(500).bodyValue("Internal server error: ${error.message}")
                }
            }
    }
    
    /**
     * Handle Stripe webhook events.
     * This endpoint receives webhook notifications from Stripe when payment events occur.
     *
     * @param request the server request containing the Stripe event data
     * @return a server response acknowledging receipt of the webhook
     */
    fun handleWebhook(request: ServerRequest): Mono<ServerResponse> {
        logger.info("Received Stripe webhook request")
        
        return request.bodyToMono<StripeWebhookRequest>()
            .flatMap { webhookRequest ->
                logger.info("Processing webhook event: ${webhookRequest.type} with ID: ${webhookRequest.id}")
                
                stripeService.handleWebhookEvent(webhookRequest)
                    .map { order ->
                        WebhookResponse(
                            received = true,
                            eventId = webhookRequest.id
                        )
                    }
                    .switchIfEmpty(
                        Mono.just(
                            WebhookResponse(
                                received = true,
                                eventId = webhookRequest.id
                            )
                        )
                    )
            }
            .flatMap { response -> ServerResponse.ok().bodyValue(response) }
            .onErrorResume { error ->
                logger.error("Error processing webhook: ${error.message}", error)
                
                val errorResponse = WebhookResponse(
                    received = false,
                    eventId = "unknown"
                )
                
                // Always return 200 OK to Stripe, even for errors
                // This prevents Stripe from retrying the webhook unnecessarily
                ServerResponse.ok().bodyValue(errorResponse)
            }
    }
}