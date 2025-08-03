package com.execodex.demolocalai.handlers

import com.execodex.demolocalai.entities.Order
import com.execodex.demolocalai.entities.OrderItem
import com.execodex.demolocalai.pojos.CreateOrderRequest
import com.execodex.demolocalai.service.OrderService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import java.net.URI

/**
 * Handler for order-related HTTP requests.
 */
@Component
class OrderHandler(private val orderService: OrderService) {

    /**
     * Create a new order with items.
     *
     * @param request the server request containing the order data
     * @return a server response containing the created order
     */
    fun createOrder(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono<CreateOrderRequest>()
            .flatMap { orderRequest -> orderService.createOrder(orderRequest) }
            .flatMap { savedOrder ->
                ServerResponse.created(URI.create("/orders/${savedOrder.id}"))
                    .bodyValue(savedOrder)
            }
            .onErrorResume { error ->
                when (error) {
                    is IllegalArgumentException -> ServerResponse.badRequest().bodyValue(error.message ?: "Bad request")
                    else -> ServerResponse.status(500).bodyValue("Internal server error: ${error.message}")
                }
            }
    }

    /**
     * Get all orders.
     *
     * @param request the server request
     * @return a server response containing all orders
     */
    fun getAllOrders(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok()
            .body(orderService.getAllOrders(), Order::class.java)
    }

    /**
     * Get an order by its ID.
     *
     * @param request the server request containing the order ID
     * @return a server response containing the order if found
     */
    fun getOrderById(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLong()
        return orderService.getOrderById(id)
            .flatMap { order -> ServerResponse.ok().bodyValue(order) }
            .switchIfEmpty(ServerResponse.notFound().build())
    }

    /**
     * Get orders by user ID.
     *
     * @param request the server request containing the user ID
     * @return a server response containing the orders if found
     */
    fun getOrdersByUserId(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("userId").toLong()
        return ServerResponse.ok()
            .body(orderService.getOrdersByUserId(userId), Order::class.java)
    }

    /**
     * Get order items by order ID.
     *
     * @param request the server request containing the order ID
     * @return a server response containing the order items if found
     */
    fun getOrderItemsByOrderId(request: ServerRequest): Mono<ServerResponse> {
        val orderId = request.pathVariable("orderId").toLong()
        return ServerResponse.ok()
            .body(orderService.getOrderItemsByOrderId(orderId), OrderItem::class.java)
    }

    /**
     * Update an existing order.
     *
     * @param request the server request containing the order ID and updated data
     * @return a server response containing the updated order if found
     */
    fun updateOrder(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLong()
        return request.bodyToMono<Order>()
            .flatMap { order -> orderService.updateOrder(id, order) }
            .flatMap { updatedOrder -> ServerResponse.ok().bodyValue(updatedOrder) }
            .switchIfEmpty(ServerResponse.notFound().build())
    }
}