package com.execodex.demolocalai.repositories

import com.execodex.demolocalai.entities.CartItem
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Reactive repository for CartItem entities.
 */
@Repository
interface CartItemRepository : ReactiveCrudRepository<CartItem, Long> {
    /**
     * Find all items in a cart.
     *
     * @param cartId the ID of the cart
     * @return a Flux of cart items
     */
    fun findByCartId(cartId: Long): Flux<CartItem>
    
    /**
     * Find a specific item in a cart by product ID.
     *
     * @param cartId the ID of the cart
     * @param productId the ID of the product
     * @return a Mono containing the cart item if found
     */
    fun findByCartIdAndProductId(cartId: Long, productId: Long): Mono<CartItem>
    
    /**
     * Delete all items in a cart.
     *
     * @param cartId the ID of the cart
     * @return a Mono completing when all items are deleted
     */
    fun deleteByCartId(cartId: Long): Mono<Void>
}