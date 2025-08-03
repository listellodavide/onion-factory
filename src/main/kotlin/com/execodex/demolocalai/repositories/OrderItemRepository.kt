package com.execodex.demolocalai.repositories

import com.execodex.demolocalai.entities.OrderItem
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

/**
 * Reactive repository for OrderItem entities.
 */
@Repository
interface OrderItemRepository : ReactiveCrudRepository<OrderItem, Long> {
    /**
     * Find order items by order ID.
     *
     * @param orderId the ID of the order
     * @return a Flux containing the order items if found
     */
    fun findByOrderId(orderId: Long): Flux<OrderItem>
    
    /**
     * Find order items by product ID.
     *
     * @param productId the ID of the product
     * @return a Flux containing the order items if found
     */
    fun findByProductId(productId: Long): Flux<OrderItem>
}