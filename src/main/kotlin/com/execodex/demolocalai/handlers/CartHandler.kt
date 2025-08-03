package com.execodex.demolocalai.handlers

import com.execodex.demolocalai.entities.Order
import com.execodex.demolocalai.pojos.AddCartItemRequest
import com.execodex.demolocalai.pojos.RemoveCartItemRequest
import com.execodex.demolocalai.service.CartService
import com.execodex.demolocalai.service.OrderService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import java.net.URI

/**
 * Handler for cart-related HTTP requests.
 */
@Component
class CartHandler(
    private val cartService: CartService,
    private val orderService: OrderService
) {

    /**
     * Get a user's cart with items.
     *
     * @param request the server request containing the user ID
     * @return a server response containing the cart with items
     */
    fun getCart(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("userId").toLong()
        return cartService.getCartWithItems(userId)
            .flatMap { cart -> ServerResponse.ok().bodyValue(cart) }
    }

    /**
     * Add an item to a user's cart.
     *
     * @param request the server request containing the user ID and item details
     * @return a server response containing the updated cart
     */
    fun addItemToCart(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("userId").toLong()
        return request.bodyToMono<AddCartItemRequest>()
            .flatMap { addRequest -> cartService.addItemToCart(userId, addRequest) }
            .flatMap { cart -> ServerResponse.ok().bodyValue(cart) }
    }

    /**
     * Remove an item from a user's cart.
     *
     * @param request the server request containing the user ID and product ID
     * @return a server response containing the updated cart
     */
    fun removeItemFromCart(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("userId").toLong()
        return request.bodyToMono<RemoveCartItemRequest>()
            .flatMap { removeRequest -> 
                cartService.removeItemFromCart(userId, removeRequest.productId)
            }
            .flatMap { cart -> ServerResponse.ok().bodyValue(cart) }
    }

    /**
     * Empty a user's cart by removing all items.
     *
     * @param request the server request containing the user ID
     * @return a server response containing the empty cart
     */
    fun emptyCart(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("userId").toLong()
        return cartService.emptyCart(userId)
            .flatMap { cart -> ServerResponse.ok().bodyValue(cart) }
    }
    
    /**
     * Checkout a user's cart by creating an order with all cart items.
     *
     * @param request the server request containing the user ID
     * @return a server response containing the created order
     */
    fun checkout(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("userId").toLong()
        
        return cartService.getCartWithItems(userId)
            .flatMap { cartResponse ->
                if (cartResponse.items.isEmpty()) {
                    return@flatMap ServerResponse.badRequest()
                        .bodyValue("Cart is empty, cannot checkout")
                }
                
                // Create order items from cart items
                val orderItems = cartResponse.items.map { cartItem ->
                    com.execodex.demolocalai.pojos.OrderItemRequest(
                        productId = cartItem.productId,
                        quantity = cartItem.quantity
                    )
                }
                
                // Create the order using the user ID directly
                orderService.createOrderForUser(userId, orderItems)
                    .flatMap { order ->
                        // Empty the cart after successful order creation
                        cartService.emptyCart(userId)
                            .thenReturn(order)
                    }
                    .flatMap { order ->
                        ServerResponse.created(URI.create("/orders/${order.id}"))
                            .bodyValue(order)
                    }
            }
            .onErrorResume { error ->
                when (error) {
                    is IllegalArgumentException -> ServerResponse.badRequest().bodyValue(error.message ?: "Bad request")
                    else -> ServerResponse.status(500).bodyValue("Internal server error: ${error.message}")
                }
            }
    }
}