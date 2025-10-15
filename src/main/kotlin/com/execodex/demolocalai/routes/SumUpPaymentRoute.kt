package com.execodex.demolocalai.config

import com.execodex.demolocalai.handler.SumUpPaymentHandler
import com.execodex.demolocalai.pojos.*
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
 * Configuration for SumUp payment-related routes.
 */
@Configuration
class SumUpPaymentRoute(private val sumUpPaymentHandler: SumUpPaymentHandler) {

    /**
     * Define routes for SumUp payment operations.
     *
     * @return a router function with defined routes
     */
    @Bean
    @RouterOperations(
        RouterOperation(
            path = "/api/sumup/checkout",
            beanClass = SumUpPaymentHandler::class,
            beanMethod = "createCheckout",
            method = [org.springframework.web.bind.annotation.RequestMethod.POST],
            operation = io.swagger.v3.oas.annotations.Operation(
                operationId = "createCheckout",
                summary = "Create a SumUp checkout",
                description = "Creates a new checkout session for payment processing",
                requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = [io.swagger.v3.oas.annotations.media.Content(
                        schema = io.swagger.v3.oas.annotations.media.Schema(implementation = CreateCheckoutRequest::class)
                    )]
                ),
                responses = [
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "Checkout created successfully",
                        content = [io.swagger.v3.oas.annotations.media.Content(
                            schema = io.swagger.v3.oas.annotations.media.Schema(implementation = CreateCheckoutResponse::class)
                        )]
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
            path = "/api/sumup/checkout/{checkoutId}",
            beanClass = SumUpPaymentHandler::class,
            beanMethod = "retrieveCheckout",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = io.swagger.v3.oas.annotations.Operation(
                operationId = "retrieveCheckout",
                summary = "Retrieve checkout status",
                description = "Retrieves the current status and details of a checkout",
                parameters = [
                    io.swagger.v3.oas.annotations.Parameter(
                        name = "checkoutId",
                        description = "The unique identifier of the checkout",
                        required = true,
                        `in` = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH
                    )
                ],
                responses = [
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "Checkout retrieved successfully",
                        content = [io.swagger.v3.oas.annotations.media.Content(
                            schema = io.swagger.v3.oas.annotations.media.Schema(implementation = RetrieveCheckoutResponse::class)
                        )]
                    ),
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description = "Checkout not found"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/api/sumup/checkout/process",
            beanClass = SumUpPaymentHandler::class,
            beanMethod = "processCheckout",
            method = [org.springframework.web.bind.annotation.RequestMethod.PUT],
            operation = io.swagger.v3.oas.annotations.Operation(
                operationId = "processCheckout",
                summary = "Process a checkout payment",
                description = "Processes the payment for an existing checkout",
                requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = [io.swagger.v3.oas.annotations.media.Content(
                        schema = io.swagger.v3.oas.annotations.media.Schema(implementation = ProcessCheckoutRequest::class)
                    )]
                ),
                responses = [
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "Payment processed successfully",
                        content = [io.swagger.v3.oas.annotations.media.Content(
                            schema = io.swagger.v3.oas.annotations.media.Schema(implementation = ProcessCheckoutResponse::class)
                        )]
                    ),
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "Bad request"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/api/sumup/checkout/{checkoutId}",
            beanClass = SumUpPaymentHandler::class,
            beanMethod = "deactivateCheckout",
            method = [org.springframework.web.bind.annotation.RequestMethod.DELETE],
            operation = io.swagger.v3.oas.annotations.Operation(
                operationId = "deactivateCheckout",
                summary = "Deactivate a checkout",
                description = "Deactivates an existing checkout, preventing further payment attempts",
                parameters = [
                    io.swagger.v3.oas.annotations.Parameter(
                        name = "checkoutId",
                        description = "The unique identifier of the checkout to deactivate",
                        required = true,
                        `in` = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH
                    )
                ],
                responses = [
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "Checkout deactivated successfully",
                        content = [io.swagger.v3.oas.annotations.media.Content(
                            schema = io.swagger.v3.oas.annotations.media.Schema(implementation = DeactivateCheckoutResponse::class)
                        )]
                    ),
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "Bad request"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/api/sumup/payment-methods",
            beanClass = SumUpPaymentHandler::class,
            beanMethod = "getPaymentMethods",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = io.swagger.v3.oas.annotations.Operation(
                operationId = "getPaymentMethods",
                summary = "Get available payment methods",
                description = "Retrieves the list of available payment methods for the merchant",
                responses = [
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "Payment methods retrieved successfully",
                        content = [io.swagger.v3.oas.annotations.media.Content(
                            schema = io.swagger.v3.oas.annotations.media.Schema(implementation = PaymentMethodsResponse::class)
                        )]
                    ),
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "500",
                        description = "Internal server error"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/api/sumup/webhook",
            beanClass = SumUpPaymentHandler::class,
            beanMethod = "handleWebhook",
            method = [org.springframework.web.bind.annotation.RequestMethod.POST],
            operation = io.swagger.v3.oas.annotations.Operation(
                operationId = "handleSumUpWebhook",
                summary = "Handle SumUp webhook events",
                description = "Processes webhook notifications from SumUp for payment events",
                requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = [io.swagger.v3.oas.annotations.media.Content(
                        schema = io.swagger.v3.oas.annotations.media.Schema(implementation = SumUpWebhookPayload::class)
                    )]
                ),
                responses = [
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "Webhook processed successfully"
                    ),
                    io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "Invalid webhook payload"
                    )
                ]
            )
        )
    )
    fun sumUpPaymentRoutes(): RouterFunction<ServerResponse> {
        return RouterFunctions.route()
            .path("/api/sumup", this::paymentRoutes)
            .build()
    }

    /**
     * Define SumUp payment-specific routes.
     *
     * @return a router function with payment routes
     */
    private fun paymentRoutes() = router {
        accept(MediaType.APPLICATION_JSON).nest {
            // Create a new checkout
            POST("/checkout", sumUpPaymentHandler::createCheckout)
            
            // Retrieve checkout status
            GET("/checkout/{checkoutId}", sumUpPaymentHandler::retrieveCheckout)
            
            // Process checkout payment
            PUT("/checkout/process", sumUpPaymentHandler::processCheckout)
            
            // Deactivate a checkout
            DELETE("/checkout/{checkoutId}", sumUpPaymentHandler::deactivateCheckout)
            
            // Get available payment methods
            GET("/payment-methods", sumUpPaymentHandler::getPaymentMethods)
            
            // Handle SumUp webhook events
            POST("/webhook", sumUpPaymentHandler::handleWebhook)
        }
    }
}
