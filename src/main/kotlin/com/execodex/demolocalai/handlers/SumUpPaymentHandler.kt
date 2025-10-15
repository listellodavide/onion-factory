package com.execodex.demolocalai.handler

import com.execodex.demolocalai.pojos.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import java.time.Instant

/**
 * Handler for SumUp payment operations
 */
@Component
class SumUpPaymentHandler(
    @Value("\${sumup.api.key}") private val apiKey: String,
    @Value("\${sumup.api.base-url:https://api.sumup.com/v0.1}") private val baseUrl: String,
    @Value("\${sumup.merchant.code}") private val merchantCode: String
) {

    private val webClient: WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader("Authorization", "Bearer $apiKey")
        .defaultHeader("Content-Type", "application/json")
        .build()

    /**
     * Create a new checkout
     */
    fun createCheckout(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono<CreateCheckoutRequest>()
            .flatMap { checkoutRequest ->
                webClient.post()
                    .uri("/checkouts")
                    .bodyValue(mapOf(
                        "checkout_reference" to checkoutRequest.checkoutReference,
                        "amount" to checkoutRequest.amount,
                        "currency" to checkoutRequest.currency,
                        "merchant_code" to checkoutRequest.merchantCode,
                        "description" to checkoutRequest.description,
                        "return_url" to checkoutRequest.returnUrl,
                        "pay_to_email" to checkoutRequest.payToEmail
                    ))
                    .retrieve()
                    .bodyToMono<CreateCheckoutResponse>()
            }
            .flatMap { response ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(response)
            }
            .onErrorResume { error ->
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .bodyValue(mapOf("error" to (error.message ?: "Failed to create checkout")))
            }
    }

    /**
     * Retrieve checkout by ID
     */
    fun retrieveCheckout(request: ServerRequest): Mono<ServerResponse> {
        val checkoutId = request.pathVariable("checkoutId")
        
        return webClient.get()
            .uri("/checkouts/$checkoutId")
            .retrieve()
            .bodyToMono<RetrieveCheckoutResponse>()
            .flatMap { response ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(response)
            }
            .onErrorResume { error ->
                ServerResponse.status(HttpStatus.NOT_FOUND)
                    .bodyValue(mapOf("error" to "Checkout not found"))
            }
    }

    /**
     * Process a checkout payment
     */
    fun processCheckout(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono<ProcessCheckoutRequest>()
            .flatMap { processRequest ->
                webClient.put()
                    .uri("/checkouts/${processRequest.checkoutId}")
                    .bodyValue(mapOf(
                        "payment_type" to processRequest.paymentType,
                        "token" to processRequest.token,
                        "installments" to processRequest.installments
                    ))
                    .retrieve()
                    .bodyToMono<ProcessCheckoutResponse>()
            }
            .flatMap { response ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(response)
            }
            .onErrorResume { error ->
                ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .bodyValue(mapOf("error" to (error.message ?: "Failed to process checkout")))
            }
    }

    /**
     * Deactivate a checkout
     */
    fun deactivateCheckout(request: ServerRequest): Mono<ServerResponse> {
        val checkoutId = request.pathVariable("checkoutId")
        
        return webClient.delete()
            .uri("/checkouts/$checkoutId")
            .retrieve()
            .bodyToMono<DeactivateCheckoutResponse>()
            .flatMap { response ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(response)
            }
            .onErrorResume { error ->
                ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .bodyValue(mapOf("error" to "Failed to deactivate checkout"))
            }
    }

    /**
     * Get available payment methods
     */
    fun getPaymentMethods(request: ServerRequest): Mono<ServerResponse> {
        return webClient.get()
            .uri("/me/payment-methods")
            .retrieve()
            .bodyToMono<PaymentMethodsResponse>()
            .flatMap { response ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(response)
            }
            .onErrorResume { error ->
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .bodyValue(mapOf("error" to "Failed to retrieve payment methods"))
            }
    }

    /**
     * Handle SumUp webhook callbacks
     */
    fun handleWebhook(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono<SumUpWebhookPayload>()
            .flatMap { payload ->
                // Process webhook event based on event type
                when (payload.eventType) {
                    "CHECKOUT_COMPLETED" -> handleCheckoutCompleted(payload)
                    "CHECKOUT_FAILED" -> handleCheckoutFailed(payload)
                    "CHECKOUT_EXPIRED" -> handleCheckoutExpired(payload)
                    else -> Mono.just(payload)
                }
            }
            .flatMap {
                ServerResponse.ok()
                    .bodyValue(mapOf("received" to true))
            }
            .onErrorResume { error ->
                ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .bodyValue(mapOf("error" to "Invalid webhook payload"))
            }
    }

    private fun handleCheckoutCompleted(payload: SumUpWebhookPayload): Mono<SumUpWebhookPayload> {
        // TODO: Implement business logic for completed checkout
        // e.g., update order status, send confirmation email, etc.
        println("Checkout completed: ${payload.checkoutId}")
        return Mono.just(payload)
    }

    private fun handleCheckoutFailed(payload: SumUpWebhookPayload): Mono<SumUpWebhookPayload> {
        // TODO: Implement business logic for failed checkout
        println("Checkout failed: ${payload.checkoutId}")
        return Mono.just(payload)
    }

    private fun handleCheckoutExpired(payload: SumUpWebhookPayload): Mono<SumUpWebhookPayload> {
        // TODO: Implement business logic for expired checkout
        println("Checkout expired: ${payload.checkoutId}")
        return Mono.just(payload)
    }
}
