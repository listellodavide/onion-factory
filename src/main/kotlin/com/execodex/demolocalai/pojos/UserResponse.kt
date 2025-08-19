package com.execodex.demolocalai.pojos

import java.time.LocalDateTime

/**
 * POJO used to return user data without exposing the password.
 */
data class UserResponse(
    val id: Long?,
    val username: String,
    val email: String,
    val createdAt: LocalDateTime,
    val pictureUrl: String? = null
)
