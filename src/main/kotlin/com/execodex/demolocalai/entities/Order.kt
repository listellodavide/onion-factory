package com.execodex.demolocalai.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table("orders")
data class Order(
    @Id
    val id: Long? = null,
    val userId: Long,
    val totalAmount: BigDecimal,
    val orderDate: LocalDateTime = LocalDateTime.now(),
    val status: String = "PENDING"
)