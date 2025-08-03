package com.execodex.demolocalai.pojos

import java.math.BigDecimal

/**
 * Request object for adding an item to a cart.
 */
data class AddCartItemRequest(
    val productId: Long,
    val quantity: Int,
    val price: BigDecimal? = null // Optional, can be calculated from product price
)