package com.execodex.demolocalai.handlers

import com.execodex.demolocalai.entities.Product
import com.execodex.demolocalai.service.ProductService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import java.net.URI

/**
 * Handler for product-related HTTP requests.
 */
@Component
class ProductHandler(private val productService: ProductService) {

    /**
     * Get all products.
     *
     * @param request the server request
     * @return a server response containing all products
     */
    fun getAllProducts(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok()
            .body(productService.getAllProducts(), Product::class.java)
    }

    /**
     * Get a product by its ID.
     *
     * @param request the server request containing the product ID
     * @return a server response containing the product if found
     */
    fun getProductById(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLong()
        return productService.getProductById(id)
            .flatMap { product -> ServerResponse.ok().bodyValue(product) }
            .switchIfEmpty(ServerResponse.notFound().build())
    }

    /**
     * Create a new product.
     *
     * @param request the server request containing the product data
     * @return a server response containing the created product
     */
    fun createProduct(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono<Product>()
            .flatMap { product -> productService.createProduct(product) }
            .flatMap { savedProduct ->
                ServerResponse.created(URI.create("/products/${savedProduct.id}"))
                    .bodyValue(savedProduct)
            }
    }

    /**
     * Update an existing product.
     *
     * @param request the server request containing the product ID and updated data
     * @return a server response containing the updated product if found
     */
    fun updateProduct(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLong()
        return request.bodyToMono<Product>()
            .flatMap { product -> productService.updateProduct(id, product) }
            .flatMap { updatedProduct -> ServerResponse.ok().bodyValue(updatedProduct) }
            .switchIfEmpty(ServerResponse.notFound().build())
    }

    /**
     * Delete a product by its ID.
     *
     * @param request the server request containing the product ID
     * @return a server response with no content if successful
     */
    fun deleteProduct(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLong()
        return productService.deleteProduct(id)
            .then(ServerResponse.noContent().build())
    }

    /**
     * Search for products by name pattern.
     *
     * @param request the server request containing the search query
     * @return a server response containing the matching products
     */
    fun searchProducts(request: ServerRequest): Mono<ServerResponse> {
        val query = request.queryParam("name").orElse("")
        return ServerResponse.ok()
            .body(productService.findProductsByNamePattern(query), Product::class.java)
    }
}