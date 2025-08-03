package com.execodex.demolocalai.pojos

data class CreateOrderRequest(
    val username: String?,
    val items: List<OrderItemRequest>,

)

data class OrderItemRequest(
    val productId: Long,
    val quantity: Int
)