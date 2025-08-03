package com.execodex.demolocalai.entities

import java.math.BigDecimal
import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * Entity representing a product in the database.
 */
@Table("products")
data class Product(
    @Id
    val id: Long? = null,
    val name: String,
    val description: String? = null,
    val price: BigDecimal,
    val quantity: Int,
    val createdAt: LocalDateTime = LocalDateTime.now()
)