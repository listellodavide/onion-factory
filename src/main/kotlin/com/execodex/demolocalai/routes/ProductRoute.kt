package com.execodex.demolocalai.routes

import com.execodex.demolocalai.handlers.ProductHandler
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
    fun productRoutes(): RouterFunction<ServerResponse> = router {
        "/products".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("", productHandler::getAllProducts)
                GET("/{id}", productHandler::getProductById)
                POST("", productHandler::createProduct)
                PUT("/{id}", productHandler::updateProduct)
                DELETE("/{id}", productHandler::deleteProduct)
                GET("/search", productHandler::searchProducts)
            }
        }
    }
}