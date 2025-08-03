package com.execodex.demolocalai.repositories

import com.execodex.demolocalai.entities.Cart
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

/**
 * Reactive repository for Cart entities.
 */
@Repository
interface CartRepository : ReactiveCrudRepository<Cart, Long> {
    /**
     * Find a cart by user ID.
     *
     * @param userId the ID of the user
     * @return a Mono containing the cart if found
     */
    fun findByUserId(userId: Long): Mono<Cart>
}