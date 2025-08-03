package com.execodex.demolocalai.entities

import java.math.BigDecimal
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * Entity representing an order item in the database.
 * This entity is used to store details of products included in an order.
 */
@Table("order_details")
data class OrderItem(
    @Id
    val id: Long? = null,
    val orderId: Long? = null,
    val productId: Long,
    val quantity: Int,
    val price: BigDecimal
)
