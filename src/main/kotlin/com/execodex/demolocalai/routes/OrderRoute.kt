package com.execodex.demolocalai.routes

import com.execodex.demolocalai.entities.Order
import com.execodex.demolocalai.entities.OrderItem
import com.execodex.demolocalai.handlers.OrderHandler
import com.execodex.demolocalai.pojos.CreateOrderRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

/**
 * Configuration for order-related routes.
 */
@Configuration
class OrderRoute(private val orderHandler: OrderHandler) {
    
    /**
     * Defines the routes for order operations.
     *
     * @return a router function with order routes
     */
    @Bean
    @RouterOperations(
        RouterOperation(
            path = "/orders",
            beanClass = OrderHandler::class,
            beanMethod = "getAllOrders",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = Operation(
                operationId = "getAllOrders",
                summary = "Get all orders",
                description = "Returns a list of all orders",
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(schema = Schema(implementation = Order::class))]
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/orders/{id}",
            beanClass = OrderHandler::class,
            beanMethod = "getOrderById",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = Operation(
                operationId = "getOrderById",
                summary = "Get order by ID",
                description = "Returns a single order by its ID",
                parameters = [
                    Parameter(
                        name = "id",
                        `in` = ParameterIn.PATH,
                        required = true,
                        description = "Order ID"
                    )
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(schema = Schema(implementation = Order::class))]
                    ),
                    ApiResponse(
                        responseCode = "404",
                        description = "Order not found"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/orders",
            beanClass = OrderHandler::class,
            beanMethod = "createOrder",
            method = [org.springframework.web.bind.annotation.RequestMethod.POST],
            operation = Operation(
                operationId = "createOrder",
                summary = "Create a new order",
                description = "Creates a new order with the provided details",
                requestBody = RequestBody(
                    required = true,
                    content = [Content(schema = Schema(implementation = CreateOrderRequest::class))]
                ),
                responses = [
                    ApiResponse(
                        responseCode = "201",
                        description = "Order created",
                        content = [Content(schema = Schema(implementation = Order::class))]
                    ),
                    ApiResponse(
                        responseCode = "400",
                        description = "Invalid input"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/orders/user/{userId}",
            beanClass = OrderHandler::class,
            beanMethod = "getOrdersByUserId",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = Operation(
                operationId = "getOrdersByUserId",
                summary = "Get orders by user ID",
                description = "Returns all orders for a specific user",
                parameters = [
                    Parameter(
                        name = "userId",
                        `in` = ParameterIn.PATH,
                        required = true,
                        description = "User ID"
                    )
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(schema = Schema(implementation = Order::class))]
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/orders/{orderId}/items",
            beanClass = OrderHandler::class,
            beanMethod = "getOrderItemsByOrderId",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = Operation(
                operationId = "getOrderItemsByOrderId",
                summary = "Get order items by order ID",
                description = "Returns all items for a specific order",
                parameters = [
                    Parameter(
                        name = "orderId",
                        `in` = ParameterIn.PATH,
                        required = true,
                        description = "Order ID"
                    )
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(schema = Schema(implementation = OrderItem::class))]
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/orders/{id}",
            beanClass = OrderHandler::class,
            beanMethod = "updateOrder",
            method = [org.springframework.web.bind.annotation.RequestMethod.PUT],
            operation = Operation(
                operationId = "updateOrder",
                summary = "Update an existing order",
                description = "Updates an order with the provided details",
                parameters = [
                    Parameter(
                        name = "id",
                        `in` = ParameterIn.PATH,
                        required = true,
                        description = "Order ID"
                    )
                ],
                requestBody = RequestBody(
                    required = true,
                    content = [Content(schema = Schema(implementation = Order::class))]
                ),
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Order updated",
                        content = [Content(schema = Schema(implementation = Order::class))]
                    ),
                    ApiResponse(
                        responseCode = "404",
                        description = "Order not found"
                    )
                ]
            )
        )
    )
    fun orderRoutes(): RouterFunction<ServerResponse> = router {
        "/orders".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("", orderHandler::getAllOrders)
                GET("/{id}", orderHandler::getOrderById)
                POST("", orderHandler::createOrder)
                PUT("/{id}", orderHandler::updateOrder)
                GET("/user/{userId}", orderHandler::getOrdersByUserId)
                GET("/{orderId}/items", orderHandler::getOrderItemsByOrderId)
            }
        }
    }
}