package com.execodex.demolocalai.repositories

import com.execodex.demolocalai.entities.Order
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Reactive repository for Order entities.
 */
@Repository
interface OrderRepository : ReactiveCrudRepository<Order, Long> {
    /**
     * Find orders by user ID.
     *
     * @param userId the ID of the user
     * @return a Flux containing the orders if found
     */
    fun findByUserId(userId: Long): Flux<Order>
    
    /**
     * Find orders by status.
     *
     * @param status the status of the orders
     * @return a Flux containing the orders if found
     */
    fun findByStatus(status: String): Flux<Order>
}