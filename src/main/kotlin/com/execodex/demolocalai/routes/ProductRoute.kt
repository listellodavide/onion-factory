package com.execodex.demolocalai.routes

import com.execodex.demolocalai.entities.Product
import com.execodex.demolocalai.handlers.ProductHandler
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

/**
 * Configuration for product-related routes.
 */
@Configuration
class ProductRoute(private val productHandler: ProductHandler) {
    
    /**
     * Defines the routes for product operations.
     *
     * @return a router function with product routes
     */
    @Bean
    @RouterOperations(
        RouterOperation(
            path = "/products",
            beanClass = ProductHandler::class,
            beanMethod = "getAllProducts",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = Operation(
                operationId = "getAllProducts",
                summary = "Get all products",
                description = "Returns a list of all products",
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(schema = Schema(implementation = Product::class))]
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/products/{id}",
            beanClass = ProductHandler::class,
            beanMethod = "getProductById",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = Operation(
                operationId = "getProductById",
                summary = "Get product by ID",
                description = "Returns a single product by its ID",
                parameters = [
                    Parameter(
                        name = "id",
                        `in` = ParameterIn.PATH,
                        required = true,
                        description = "Product ID"
                    )
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(schema = Schema(implementation = Product::class))]
                    ),
                    ApiResponse(
                        responseCode = "404",
                        description = "Product not found"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/products",
            beanClass = ProductHandler::class,
            beanMethod = "createProduct",
            method = [org.springframework.web.bind.annotation.RequestMethod.POST],
            operation = Operation(
                operationId = "createProduct",
                summary = "Create a new product",
                description = "Creates a new product with the provided details",
                requestBody = RequestBody(
                    required = true,
                    content = [Content(schema = Schema(implementation = Product::class))]
                ),
                responses = [
                    ApiResponse(
                        responseCode = "201",
                        description = "Product created",
                        content = [Content(schema = Schema(implementation = Product::class))]
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/products/{id}",
            beanClass = ProductHandler::class,
            beanMethod = "updateProduct",
            method = [org.springframework.web.bind.annotation.RequestMethod.PUT],
            operation = Operation(
                operationId = "updateProduct",
                summary = "Update an existing product",
                description = "Updates a product with the provided details",
                parameters = [
                    Parameter(
                        name = "id",
                        `in` = ParameterIn.PATH,
                        required = true,
                        description = "Product ID"
                    )
                ],
                requestBody = RequestBody(
                    required = true,
                    content = [Content(schema = Schema(implementation = Product::class))]
                ),
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Product updated",
                        content = [Content(schema = Schema(implementation = Product::class))]
                    ),
                    ApiResponse(
                        responseCode = "404",
                        description = "Product not found"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/products/{id}",
            beanClass = ProductHandler::class,
            beanMethod = "deleteProduct",
            method = [org.springframework.web.bind.annotation.RequestMethod.DELETE],
            operation = Operation(
                operationId = "deleteProduct",
                summary = "Delete a product",
                description = "Deletes a product by its ID",
                parameters = [
                    Parameter(
                        name = "id",
                        `in` = ParameterIn.PATH,
                        required = true,
                        description = "Product ID"
                    )
                ],
                responses = [
                    ApiResponse(
                        responseCode = "204",
                        description = "Product deleted"
                    ),
                    ApiResponse(
                        responseCode = "404",
                        description = "Product not found"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/products/search",
            beanClass = ProductHandler::class,
            beanMethod = "searchProducts",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = Operation(
                operationId = "searchProducts",
                summary = "Search products by name",
                description = "Returns products matching the search query",
                parameters = [
                    Parameter(
                        name = "name",
                        `in` = ParameterIn.QUERY,
                        required = false,
                        description = "Name pattern to search for"
                    )
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(schema = Schema(implementation = Product::class))]
                    )
                ]
            )
        )
    )
    fun productRoutes(): RouterFunction<ServerResponse> = router {
        "/products".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("", productHandler::getAllProducts)
                GET("/search", productHandler::searchProducts)
                GET("/{id}", productHandler::getProductById)
                POST("", productHandler::createProduct)
                PUT("/{id}", productHandler::updateProduct)
                DELETE("/{id}", productHandler::deleteProduct)
            }
        }
    }
}