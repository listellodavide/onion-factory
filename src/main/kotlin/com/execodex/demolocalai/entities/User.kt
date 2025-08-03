package com.execodex.demolocalai.entities

import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * Entity representing a user in the database.
 */
@Table("users")
data class User(
    @Id
    val id: Long? = null,
    val username: String,
    val password: String,
    val email: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)