package com.execodex.demolocalai.repositories

import com.execodex.demolocalai.entities.Product
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Reactive repository for Product entities.
 */
@Repository
interface ProductRepository : ReactiveCrudRepository<Product, Long> {
    /**
     * Find a product by its name.
     *
     * @param name the name of the product
     * @return a Mono containing the product if found
     */
    fun findByName(name: String): Mono<Product>
    
    /**
     * Find products by partial name match.
     *
     * @param namePattern the pattern to match against product names
     * @return a Flux of products matching the pattern
     */
    fun findByNameContainingIgnoreCase(namePattern: String): Flux<Product>

    /**
     * Find a product by its SKU.
     *
     * @param sku the SKU of the product
     * @return a Mono containing the product if found
     */
    fun findBySku(sku: String): Mono<Product>
    
    /**
     * Find a product by its slug.
     *
     * @param slug the slug of the product
     * @return a Mono containing the product if found
     */
    fun findBySlug(slug: String): Mono<Product>
    }