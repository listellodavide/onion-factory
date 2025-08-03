package com.execodex.demolocalai.service

import com.execodex.demolocalai.entities.Order
import com.execodex.demolocalai.entities.OrderItem
import com.execodex.demolocalai.pojos.CreateOrderRequest
import com.execodex.demolocalai.repositories.OrderRepository
import com.execodex.demolocalai.repositories.OrderItemRepository
import com.execodex.demolocalai.repositories.ProductRepository
import com.execodex.demolocalai.repositories.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Service for managing orders.
 */
@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) {

    /**
     * Create a new order with items.
     *
     * @param createOrderRequest the order request containing user and items information
     * @return a Mono containing the created order
     */
    fun createOrder(createOrderRequest: CreateOrderRequest): Mono<Order> {
        // Collect product information for all items
        val productItemsMono = Flux.fromIterable(createOrderRequest.items)
            .flatMap { item ->
                productRepository.findById(item.productId)
                    .switchIfEmpty(Mono.error(IllegalArgumentException("Product not found: ${item.productId}")))
                    .map { product -> Pair(product, item.quantity) }
            }
            .collectList()

        // Get user ID if username is provided
        val userIdMono = if (createOrderRequest.username != null) {
            userRepository.findByUsername(createOrderRequest.username)
                .switchIfEmpty(Mono.error(IllegalArgumentException("User not found")))
                .map { it.id!! }
        } else {
            Mono.just(0L) // Default user ID if not provided
        }

        // Calculate total amount and create order
        return Mono.zip(userIdMono, productItemsMono)
            .flatMap { tuple ->
                val userId = tuple.t1
                val productItems = tuple.t2
                
                // Calculate total amount
                val totalAmount = productItems.fold(BigDecimal.ZERO) { acc, pair ->
                    val (product, quantity) = pair
                    acc.add(product.price.multiply(BigDecimal(quantity)))
                }
                
                // Create and save the order
                val order = Order(
                    userId = userId,
                    totalAmount = totalAmount,
                    orderDate = LocalDateTime.now(),
                    status = "PENDING"
                )
                
                orderRepository.save(order)
                    .flatMap { savedOrder ->
                        // Create and save order items
                        val orderItems = productItems.map { pair ->
                            val (product, quantity) = pair
                            OrderItem(
                                orderId = savedOrder.id,
                                productId = product.id!!,
                                quantity = quantity,
                                price = product.price
                            )
                        }
                        
                        Flux.fromIterable(orderItems)
                            .flatMap { orderItem -> orderItemRepository.save(orderItem) }
                            .collectList()
                            .thenReturn(savedOrder)
                    }
            }
    }

    /**
     * Get all orders.
     *
     * @return a Flux of all orders
     */
    fun getAllOrders(): Flux<Order> = orderRepository.findAll()

    /**
     * Get an order by its ID.
     *
     * @param id the ID of the order
     * @return a Mono containing the order if found
     */
    fun getOrderById(id: Long): Mono<Order> = orderRepository.findById(id)

    /**
     * Get orders by user ID.
     *
     * @param userId the ID of the user
     * @return a Flux containing the orders if found
     */
    fun getOrdersByUserId(userId: Long): Flux<Order> = orderRepository.findByUserId(userId)

    /**
     * Get order items by order ID.
     *
     * @param orderId the ID of the order
     * @return a Flux containing the order items if found
     */
    fun getOrderItemsByOrderId(orderId: Long): Flux<OrderItem> = orderItemRepository.findByOrderId(orderId)
}