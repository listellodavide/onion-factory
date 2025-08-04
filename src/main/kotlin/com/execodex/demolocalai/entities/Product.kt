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
    val sku: String,
    val name: String,
    val slug: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val price: BigDecimal,
    val quantity: Int,
    val createdAt: LocalDateTime = LocalDateTime.now()
)