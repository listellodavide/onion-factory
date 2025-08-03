package com.execodex.demolocalai.service

import com.execodex.demolocalai.entities.Product
import com.execodex.demolocalai.repositories.ProductRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service for managing products.
 */
@Service
class ProductService(private val productRepository: ProductRepository) {

    /**
     * Get all products.
     *
     * @return a Flux of all products
     */
    fun getAllProducts(): Flux<Product> = productRepository.findAll()

    /**
     * Get a product by its ID.
     *
     * @param id the ID of the product
     * @return a Mono containing the product if found
     */
    fun getProductById(id: Long): Mono<Product> = productRepository.findById(id)

    /**
     * Create a new product.
     *
     * @param product the product to create
     * @return a Mono containing the created product
     */
    fun createProduct(product: Product): Mono<Product> = productRepository.save(product)

    /**
     * Update an existing product.
     *
     * @param id the ID of the product to update
     * @param product the updated product data
     * @return a Mono containing the updated product if found
     */
    fun updateProduct(id: Long, product: Product): Mono<Product> {
        return productRepository.findById(id)
            .flatMap { existingProduct ->
                val updatedProduct = Product(
                    id = existingProduct.id,
                    name = product.name,
                    description = product.description,
                    price = product.price,
                    quantity = product.quantity,
                    createdAt = existingProduct.createdAt
                )
                productRepository.save(updatedProduct)
            }
    }

    /**
     * Delete a product by its ID.
     *
     * @param id the ID of the product to delete
     * @return a Mono completing when the product is deleted
     */
    fun deleteProduct(id: Long): Mono<Void> = productRepository.deleteById(id)

    /**
     * Find products by name pattern.
     *
     * @param namePattern the pattern to match against product names
     * @return a Flux of products matching the pattern
     */
    fun findProductsByNamePattern(namePattern: String): Flux<Product> = 
        productRepository.findByNameContainingIgnoreCase(namePattern)
}