package com.execodex.demolocalai.routes

import com.execodex.demolocalai.handlers.StripePaymentHandler
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

/**
 * Configuration for Stripe payment-related routes.
 */
@Configuration
class StripePaymentRoute(private val stripePaymentHandler: StripePaymentHandler) {

    /**
     * Define routes for Stripe payment operations.
     *
     * @return a router function with defined routes
     */
    @Bean
    @RouterOperations(
        RouterOperation(
            path = "/api/payments/create-intent",
            beanClass = StripePaymentHandler::class,
            beanMethod = "createPaymentIntent",
            method = [org.springframework.web.bind.annotation.RequestMethod.POST],
            operation = io.swagger.v3.oas.annotations.Operation(
                operationId = "createPaymentIntent",
                summary = "Create a payment intent",
                description = "Creates a payment intent for an order",
                requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = [io.swagger.v3.oas.annotations.media.Content(
                        schema = io.swagger.v3.oas.annotations.media.Schema(implementation = com.execodex.demolocalai.pojos.CreatePaymentIntentRequest::class)
                    )]
                ),
                responses = [
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "Payment intent created successfully",
                        content = [io.swagger.v3.oas.annotations.media.Content(
                            schema = io.swagger.v3.oas.annotations.media.Schema(implementation = com.execodex.demolocalai.pojos.PaymentIntentResponse::class)
                        )
                        ]
                    ),
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "Bad request"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/api/payments/confirm",
            beanClass = StripePaymentHandler::class,
            beanMethod = "confirmPayment",
            method = [org.springframework.web.bind.annotation.RequestMethod.POST],
            operation = io.swagger.v3.oas.annotations.Operation(
                operationId = "confirmPayment",
                summary = "Confirm a payment",
                description = "Confirms a payment was successful and updates the order status",
                requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = [io.swagger.v3.oas.annotations.media.Content(
                        schema = io.swagger.v3.oas.annotations.media.Schema(implementation = com.execodex.demolocalai.pojos.ConfirmPaymentRequest::class)
                    )]
                ),
                responses = [
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "Payment confirmed successfully",
                        content = [io.swagger.v3.oas.annotations.media.Content(
                            schema = io.swagger.v3.oas.annotations.media.Schema(implementation = com.execodex.demolocalai.pojos.PaymentConfirmationResponse::class)
                        )
                        ]
                    ),
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "Bad request"
                    ),
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "500",
                        description = "Internal server error"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/api/payments/create-checkout",
            beanClass = StripePaymentHandler::class,
            beanMethod = "createCheckoutSession",
            method = [org.springframework.web.bind.annotation.RequestMethod.POST],
            operation = io.swagger.v3.oas.annotations.Operation(
                operationId = "createCheckoutSession",
                summary = "Create a checkout session",
                description = "Creates a Stripe checkout session for an order",
                requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = [io.swagger.v3.oas.annotations.media.Content(
                        schema = io.swagger.v3.oas.annotations.media.Schema(implementation = com.execodex.demolocalai.pojos.CreateCheckoutSessionRequest::class)
                    )]
                ),
                responses = [
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "Checkout session created successfully",
                        content = [io.swagger.v3.oas.annotations.media.Content(
                            schema = io.swagger.v3.oas.annotations.media.Schema(implementation = com.execodex.demolocalai.pojos.CheckoutSessionResponse::class)
                        )
                        ]
                    ),
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "Bad request"
                    ),
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "500",
                        description = "Internal server error"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/api/payments/webhook",
            beanClass = StripePaymentHandler::class,
            beanMethod = "handleWebhook",
            method = [org.springframework.web.bind.annotation.RequestMethod.POST],
            operation = io.swagger.v3.oas.annotations.Operation(
                operationId = "handleWebhook",
                summary = "Handle Stripe webhook events",
                description = "Processes webhook notifications from Stripe for payment events",
                requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = [io.swagger.v3.oas.annotations.media.Content(
                        schema = io.swagger.v3.oas.annotations.media.Schema(implementation = com.execodex.demolocalai.pojos.StripeWebhookRequest::class)
                    )]
                ),
                responses = [
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "Webhook processed successfully",
                        content = [io.swagger.v3.oas.annotations.media.Content(
                            schema = io.swagger.v3.oas.annotations.media.Schema(implementation = com.execodex.demolocalai.pojos.WebhookResponse::class)
                        )
                        ]
                    )
                ]
            )
        )
    )

    fun stripePaymentRoutes(): RouterFunction<ServerResponse> {
        return RouterFunctions.route()
            .path("/api/payments", this::paymentRoutes)
            .build()
    }

    /**
     * Define payment-specific routes.
     *
     * @return a router function with payment routes
     */
    private fun paymentRoutes() = router {
        accept(MediaType.APPLICATION_JSON).nest {
            // Handle Stripe webhook events
            POST("/webhook", stripePaymentHandler::handleWebhook)
            // Create a payment intent for an order
            POST("/create-intent", stripePaymentHandler::createPaymentIntent)

            // Confirm a payment was successful
            POST("/confirm", stripePaymentHandler::confirmPayment)

            // Create a checkout session for an order
            POST("/create-checkout", stripePaymentHandler::createCheckoutSession)

        }
    }
}