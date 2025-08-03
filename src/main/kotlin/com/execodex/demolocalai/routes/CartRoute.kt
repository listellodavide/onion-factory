package com.execodex.demolocalai.routes

import com.execodex.demolocalai.entities.Cart
import com.execodex.demolocalai.handlers.CartHandler
import com.execodex.demolocalai.pojos.AddCartItemRequest
import com.execodex.demolocalai.pojos.CartResponse
import com.execodex.demolocalai.pojos.RemoveCartItemRequest
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
 * Configuration for cart-related routes.
 */
@Configuration
class CartRoute(private val cartHandler: CartHandler) {
    
    /**
     * Defines the routes for cart operations.
     *
     * @return a router function with cart routes
     */
    @Bean
    @RouterOperations(
        RouterOperation(
            path = "/users/{userId}/cart",
            beanClass = CartHandler::class,
            beanMethod = "getCart",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = Operation(
                operationId = "getCart",
                summary = "Get user's cart",
                description = "Returns the cart with items for a specific user",
                parameters = [
                    Parameter(
                        name = "userId",
                        `in` = ParameterIn.PATH,
                        required = true,
                        description = "User ID"
                    )
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(schema = Schema(implementation = CartResponse::class))]
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/users/{userId}/cart/items",
            beanClass = CartHandler::class,
            beanMethod = "addItemToCart",
            method = [org.springframework.web.bind.annotation.RequestMethod.POST],
            operation = Operation(
                operationId = "addItemToCart",
                summary = "Add item to cart",
                description = "Adds an item to the user's cart",
                parameters = [
                    Parameter(
                        name = "userId",
                        `in` = ParameterIn.PATH,
                        required = true,
                        description = "User ID"
                    )
                ],
                requestBody = RequestBody(
                    required = true,
                    content = [Content(schema = Schema(implementation = AddCartItemRequest::class))]
                ),
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Item added to cart",
                        content = [Content(schema = Schema(implementation = CartResponse::class))]
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/users/{userId}/cart/items",
            beanClass = CartHandler::class,
            beanMethod = "removeItemFromCart",
            method = [org.springframework.web.bind.annotation.RequestMethod.DELETE],
            operation = Operation(
                operationId = "removeItemFromCart",
                summary = "Remove item from cart",
                description = "Removes an item from the user's cart",
                parameters = [
                    Parameter(
                        name = "userId",
                        `in` = ParameterIn.PATH,
                        required = true,
                        description = "User ID"
                    )
                ],
                requestBody = RequestBody(
                    required = true,
                    content = [Content(schema = Schema(implementation = RemoveCartItemRequest::class))]
                ),
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Item removed from cart",
                        content = [Content(schema = Schema(implementation = CartResponse::class))]
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/users/{userId}/cart",
            beanClass = CartHandler::class,
            beanMethod = "emptyCart",
            method = [org.springframework.web.bind.annotation.RequestMethod.DELETE],
            operation = Operation(
                operationId = "emptyCart",
                summary = "Empty cart",
                description = "Removes all items from the user's cart",
                parameters = [
                    Parameter(
                        name = "userId",
                        `in` = ParameterIn.PATH,
                        required = true,
                        description = "User ID"
                    )
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Cart emptied",
                        content = [Content(schema = Schema(implementation = CartResponse::class))]
                    )
                ]
            )
        )
    )
    fun cartRoutes(): RouterFunction<ServerResponse> = router {
        "/users/{userId}".nest {
            "/cart".nest {
                accept(MediaType.APPLICATION_JSON).nest {
                    GET("", cartHandler::getCart)
                    POST("/items", cartHandler::addItemToCart)
                    DELETE("/items", cartHandler::removeItemFromCart)
                    DELETE("", cartHandler::emptyCart)
                }
            }
        }
    }
}