package com.execodex.demolocalai.routes

import com.execodex.demolocalai.handlers.OrderHandler
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
    fun orderRoutes(): RouterFunction<ServerResponse> = router {
        "/orders".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("", orderHandler::getAllOrders)
                GET("/{id}", orderHandler::getOrderById)
                POST("", orderHandler::createOrder)
                GET("/user/{userId}", orderHandler::getOrdersByUserId)
                GET("/{orderId}/items", orderHandler::getOrderItemsByOrderId)
            }
        }
    }
}