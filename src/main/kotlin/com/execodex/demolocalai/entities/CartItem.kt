package com.execodex.demolocalai.entities

import java.math.BigDecimal
import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * Entity representing an item in a shopping cart in the database.
 */
@Table("cart_items")
data class CartItem(
    @Id
    val id: Long? = null,
    val cartId: Long,
    val productId: Long,
    val quantity: Int,
    val price: BigDecimal,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)