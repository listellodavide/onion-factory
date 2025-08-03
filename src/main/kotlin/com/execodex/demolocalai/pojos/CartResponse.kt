package com.execodex.demolocalai.pojos

import com.execodex.demolocalai.entities.CartItem
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Response object for cart operations.
 */
data class CartResponse(
    val id: Long,
    val userId: Long,
    val items: List<CartItemResponse>,
    val totalPrice: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

/**
 * Response object for cart item details.
 */
data class CartItemResponse(
    val id: Long,
    val productId: Long,
    val quantity: Int,
    val price: BigDecimal,
    val totalPrice: BigDecimal
) {
    companion object {
        fun fromCartItem(cartItem: CartItem): CartItemResponse {
            return CartItemResponse(
                id = cartItem.id ?: 0,
                productId = cartItem.productId,
                quantity = cartItem.quantity,
                price = cartItem.price,
                totalPrice = cartItem.price.multiply(BigDecimal(cartItem.quantity))
            )
        }
    }
}