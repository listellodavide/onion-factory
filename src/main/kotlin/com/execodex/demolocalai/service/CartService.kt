package com.execodex.demolocalai.service

import com.execodex.demolocalai.entities.Cart
import com.execodex.demolocalai.entities.CartItem
import com.execodex.demolocalai.pojos.AddCartItemRequest
import com.execodex.demolocalai.pojos.CartItemResponse
import com.execodex.demolocalai.pojos.CartResponse
import com.execodex.demolocalai.repositories.CartItemRepository
import com.execodex.demolocalai.repositories.CartRepository
import com.execodex.demolocalai.repositories.ProductRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Service for managing shopping carts.
 */
@Service
class CartService(
    private val cartRepository: CartRepository,
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository
) {

    /**
     * Get a cart by user ID, creating one if it doesn't exist.
     *
     * @param userId the ID of the user
     * @return a Mono containing the cart
     */
    fun getOrCreateCart(userId: Long): Mono<Cart> {
        return cartRepository.findByUserId(userId)
            .switchIfEmpty(
                cartRepository.save(
                    Cart(
                        userId = userId,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                )
            )
    }

    /**
     * Get a cart with its items by user ID.
     *
     * @param userId the ID of the user
     * @return a Mono containing the cart response with items
     */
    fun getCartWithItems(userId: Long): Mono<CartResponse> {
        return getOrCreateCart(userId)
            .flatMap { cart ->
                cartItemRepository.findByCartId(cart.id!!)
                    .map { CartItemResponse.fromCartItem(it) }
                    .collectList()
                    .map { items ->
                        CartResponse(
                            id = cart.id,
                            userId = cart.userId,
                            items = items,
                            totalPrice = items.fold(BigDecimal.ZERO) { acc, item -> acc.add(item.totalPrice) },
                            createdAt = cart.createdAt,
                            updatedAt = cart.updatedAt
                        )
                    }
            }
    }

    /**
     * Add an item to a cart.
     *
     * @param userId the ID of the user
     * @param request the add cart item request
     * @return a Mono containing the updated cart response
     */
    fun addItemToCart(userId: Long, request: AddCartItemRequest): Mono<CartResponse> {
        return getOrCreateCart(userId)
            .flatMap { cart ->
                // Check if product exists and get its price if not provided
                productRepository.findById(request.productId)
                    .flatMap { product ->
                        val price = request.price ?: product.price
                        
                        // Check if item already exists in cart
                        cartItemRepository.findByCartIdAndProductId(cart.id!!, request.productId)
                            .flatMap { existingItem ->
                                // Update existing item quantity
                                val updatedItem = CartItem(
                                    id = existingItem.id,
                                    cartId = existingItem.cartId,
                                    productId = existingItem.productId,
                                    quantity = existingItem.quantity + request.quantity,
                                    price = price,
                                    createdAt = existingItem.createdAt,
                                    updatedAt = LocalDateTime.now()
                                )
                                cartItemRepository.save(updatedItem)
                            }
                            .switchIfEmpty(
                                // Create new cart item
                                cartItemRepository.save(
                                    CartItem(
                                        cartId = cart.id,
                                        productId = request.productId,
                                        quantity = request.quantity,
                                        price = price,
                                        createdAt = LocalDateTime.now(),
                                        updatedAt = LocalDateTime.now()
                                    )
                                )
                            )
                            .then(getCartWithItems(userId))
                    }
            }
    }

    /**
     * Remove an item from a cart.
     *
     * @param userId the ID of the user
     * @param productId the ID of the product to remove
     * @return a Mono containing the updated cart response
     */
    fun removeItemFromCart(userId: Long, productId: Long): Mono<CartResponse> {
        return getOrCreateCart(userId)
            .flatMap { cart ->
                cartItemRepository.findByCartIdAndProductId(cart.id!!, productId)
                    .flatMap { cartItemRepository.delete(it) }
                    .then(getCartWithItems(userId))
            }
    }

    /**
     * Empty a cart by removing all items.
     *
     * @param userId the ID of the user
     * @return a Mono containing the empty cart response
     */
    fun emptyCart(userId: Long): Mono<CartResponse> {
        return getOrCreateCart(userId)
            .flatMap { cart ->
                cartItemRepository.deleteByCartId(cart.id!!)
                    .then(getCartWithItems(userId))
            }
    }
}