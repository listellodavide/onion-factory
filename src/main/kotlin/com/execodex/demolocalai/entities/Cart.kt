package com.execodex.demolocalai.entities

import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * Entity representing a shopping cart in the database.
 */
@Table("cart")
data class Cart(
    @Id
    val id: Long? = null,
    val userId: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)