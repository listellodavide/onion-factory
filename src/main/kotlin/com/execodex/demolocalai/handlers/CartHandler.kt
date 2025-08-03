package com.execodex.demolocalai.handlers

import com.execodex.demolocalai.pojos.AddCartItemRequest
import com.execodex.demolocalai.pojos.RemoveCartItemRequest
import com.execodex.demolocalai.service.CartService
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
class CartHandler(private val cartService: CartService) {

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
}