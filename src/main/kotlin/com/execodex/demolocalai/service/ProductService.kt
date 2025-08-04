package com.execodex.demolocalai.service

import com.execodex.demolocalai.entities.Product
import com.execodex.demolocalai.repositories.ProductRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.text.Normalizer
import java.util.Locale

/**
 * Service for managing products.
 */
@Service
class ProductService(private val productRepository: ProductRepository) {

    /**
     * Generate a base URL-friendly slug from a product name.
     *
     * @param name the product name
     * @return a URL-friendly slug
     */
    private fun generateBaseSlug(name: String): String {
        return Normalizer.normalize(name, Normalizer.Form.NFD)
            .lowercase(Locale.getDefault())
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .replace("[^a-z0-9\\s-]".toRegex(), "")
            .replace("\\s+".toRegex(), "-")
            .replace("-+".toRegex(), "-")
            .trim('-')
    }
    
    /**
     * Generate a unique URL-friendly slug from a product name.
     * If a product with the generated slug already exists, a number is appended to make it unique.
     *
     * @param name the product name
     * @return a Mono containing a unique URL-friendly slug
     */
    private fun generateSlug(name: String): Mono<String> {
        val baseSlug = generateBaseSlug(name)
        
        return checkSlugUniqueness(baseSlug, 0)
    }
    
    /**
     * Recursively check if a slug is unique and append a number if needed.
     *
     * @param baseSlug the base slug to check
     * @param counter the counter to append (0 means no counter)
     * @return a Mono containing a unique slug
     */
    private fun checkSlugUniqueness(baseSlug: String, counter: Int): Mono<String> {
        val slugToCheck = if (counter > 0) "$baseSlug-$counter" else baseSlug
        
        return productRepository.findBySlug(slugToCheck)
            .flatMap { 
                // Slug exists, try with next counter
                checkSlugUniqueness(baseSlug, counter + 1)
            }
            .switchIfEmpty(Mono.just(slugToCheck))
    }

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
     * Checks if a product with the same SKU already exists before saving.
     * Generates a unique slug based on the product name.
     *
     * @param product the product to create
     * @return a Mono containing the created product, or an error if a product with the same SKU already exists
     */
    fun createProduct(product: Product): Mono<Product> {
        return productRepository.findBySku(product.sku)
            .flatMap<Product> {
                Mono.error(IllegalArgumentException("A product with SKU ${product.sku} already exists"))
            }
            .switchIfEmpty(
                generateSlug(product.name).flatMap { uniqueSlug ->
                    val productWithSlug = Product(
                        sku = product.sku,
                        name = product.name,
                        slug = uniqueSlug,
                        description = product.description,
                        imageUrl = product.imageUrl,
                        price = product.price,
                        quantity = product.quantity,
                        createdAt = product.createdAt
                    )
                    productRepository.save(productWithSlug)
                }
            )
    }

    /**
     * Update an existing product.
     * Generates a new unique slug based on the updated product name.
     *
     * @param id the ID of the product to update
     * @param product the updated product data
     * @return a Mono containing the updated product if found
     */
    fun updateProduct(id: Long, product: Product): Mono<Product> {
        return productRepository.findById(id)
            .flatMap { existingProduct ->
                generateSlug(product.name).flatMap { uniqueSlug ->
                    val updatedProduct = Product(
                        id = existingProduct.id,
                        sku = product.sku,
                        name = product.name,
                        slug = uniqueSlug,
                        description = product.description,
                        imageUrl = product.imageUrl,
                        price = product.price,
                        quantity = product.quantity,
                        createdAt = existingProduct.createdAt
                    )
                    productRepository.save(updatedProduct)
                }
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

    fun getProductBySlug(slug: String): Mono<Product> {
        return productRepository.findBySlug(slug)
            .switchIfEmpty(Mono.error(IllegalArgumentException("Product with slug '$slug' not found")))
    }
}