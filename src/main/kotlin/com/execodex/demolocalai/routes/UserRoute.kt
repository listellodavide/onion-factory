package com.execodex.demolocalai.routes

import com.execodex.demolocalai.entities.User
import com.execodex.demolocalai.handlers.UserHandler
import com.execodex.demolocalai.pojos.GoogleUserInfo
import com.execodex.demolocalai.pojos.GithubUserInfo
import com.execodex.demolocalai.pojos.UserResponse
import com.execodex.demolocalai.pojos.AuthProviderResponse
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
 * Configuration for user-related routes.
 */
@Configuration
class UserRoute(private val userHandler: UserHandler) {
    
    /**
     * Defines the routes for user operations.
     *
     * @return a router function with user routes
     */
    @Bean
    @RouterOperations(
        RouterOperation(
            path = "/users",
            beanClass = UserHandler::class,
            beanMethod = "getAllUsers",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = Operation(
                operationId = "getAllUsers",
                summary = "Get all users",
                description = "Returns a list of all users",
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(schema = Schema(implementation = User::class))]
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/users/{id}",
            beanClass = UserHandler::class,
            beanMethod = "getUserById",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = Operation(
                operationId = "getUserById",
                summary = "Get user by ID",
                description = "Returns a single user by its ID",
                parameters = [
                    Parameter(
                        name = "id",
                        `in` = ParameterIn.PATH,
                        required = true,
                        description = "User ID"
                    )
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(schema = Schema(implementation = User::class))]
                    ),
                    ApiResponse(
                        responseCode = "404",
                        description = "User not found"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/users",
            beanClass = UserHandler::class,
            beanMethod = "createUser",
            method = [org.springframework.web.bind.annotation.RequestMethod.POST],
            operation = Operation(
                operationId = "createUser",
                summary = "Create a new user",
                description = "Creates a new user with the provided details",
                requestBody = RequestBody(
                    required = true,
                    content = [Content(schema = Schema(implementation = User::class))]
                ),
                responses = [
                    ApiResponse(
                        responseCode = "201",
                        description = "User created",
                        content = [Content(schema = Schema(implementation = User::class))]
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/users/{id}",
            beanClass = UserHandler::class,
            beanMethod = "updateUser",
            method = [org.springframework.web.bind.annotation.RequestMethod.PUT],
            operation = Operation(
                operationId = "updateUser",
                summary = "Update an existing user",
                description = "Updates a user with the provided details",
                parameters = [
                    Parameter(
                        name = "id",
                        `in` = ParameterIn.PATH,
                        required = true,
                        description = "User ID"
                    )
                ],
                requestBody = RequestBody(
                    required = true,
                    content = [Content(schema = Schema(implementation = User::class))]
                ),
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "User updated",
                        content = [Content(schema = Schema(implementation = User::class))]
                    ),
                    ApiResponse(
                        responseCode = "404",
                        description = "User not found"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/users/{id}",
            beanClass = UserHandler::class,
            beanMethod = "deleteUser",
            method = [org.springframework.web.bind.annotation.RequestMethod.DELETE],
            operation = Operation(
                operationId = "deleteUser",
                summary = "Delete a user",
                description = "Deletes a user by its ID",
                parameters = [
                    Parameter(
                        name = "id",
                        `in` = ParameterIn.PATH,
                        required = true,
                        description = "User ID"
                    )
                ],
                responses = [
                    ApiResponse(
                        responseCode = "204",
                        description = "User deleted"
                    ),
                    ApiResponse(
                        responseCode = "404",
                        description = "User not found"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/users/search",
            beanClass = UserHandler::class,
            beanMethod = "searchUsers",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = Operation(
                operationId = "searchUsers",
                summary = "Search users by username",
                description = "Returns users matching the search query",
                parameters = [
                    Parameter(
                        name = "username",
                        `in` = ParameterIn.QUERY,
                        required = false,
                        description = "Username pattern to search for"
                    )
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(schema = Schema(implementation = User::class))]
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/users/username/{username}",
            beanClass = UserHandler::class,
            beanMethod = "getUserByUsername",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = Operation(
                operationId = "getUserByUsername",
                summary = "Get user by username",
                description = "Returns a single user by its username",
                parameters = [
                    Parameter(
                        name = "username",
                        `in` = ParameterIn.PATH,
                        required = true,
                        description = "Username"
                    )
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(schema = Schema(implementation = User::class))]
                    ),
                    ApiResponse(
                        responseCode = "404",
                        description = "User not found"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/users/email",
            beanClass = UserHandler::class,
            beanMethod = "getUserByEmail",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = Operation(
                operationId = "getUserByEmail",
                summary = "Get user by email",
                description = "Returns a single user by its email",
                parameters = [
                    Parameter(
                        name = "email",
                        `in` = ParameterIn.QUERY,
                        required = true,
                        description = "Email address"
                    )
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(schema = Schema(implementation = User::class))]
                    ),
                    ApiResponse(
                        responseCode = "404",
                        description = "User not found"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/users/me",
            beanClass = UserHandler::class,
            beanMethod = "getCurrentOAuth2User",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = Operation(
                operationId = "getCurrentOAuth2User",
                summary = "Get current Google user info or Github user info",
                description = "Returns OAuth2/OIDC information about the currently logged-in Google or Github user. The 200 response body can be either GoogleUserInfo or GithubUserInfo depending on the provider; clients should handle both. Returns 401 if not authenticated.",
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(schema = Schema(oneOf = [GoogleUserInfo::class, GithubUserInfo::class]))]
                    ),
                    ApiResponse(
                        responseCode = "401",
                        description = "Unauthenticated"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/users/me/provider",
            beanClass = UserHandler::class,
            beanMethod = "getAuthProvider",
            method = [org.springframework.web.bind.annotation.RequestMethod.GET],
            operation = Operation(
                operationId = "getAuthProvider",
                summary = "Get current authentication provider",
                description = "Returns the OAuth2/OIDC provider name for the currently authenticated user (e.g., google, github). Returns 401 if not authenticated.",
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(schema = Schema(implementation = AuthProviderResponse::class))]
                    ),
                    ApiResponse(
                        responseCode = "401",
                        description = "Unauthenticated"
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/users/ensure-from-me",
            beanClass = UserHandler::class,
            beanMethod = "ensureUserFromMe",
            method = [org.springframework.web.bind.annotation.RequestMethod.POST],
            operation = Operation(
                operationId = "ensureUserFromMe",
                summary = "Ensure current user from principal",
                description = "Ensures the request is authenticated, extracts email and profile from principal, and returns the application user (creates it if not present)." ,
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "User returned",
                        content = [Content(schema = Schema(implementation = UserResponse::class))]
                    ),
                    ApiResponse(
                        responseCode = "400",
                        description = "Email missing in principal"
                    ),
                    ApiResponse(
                        responseCode = "401",
                        description = "Unauthenticated"
                    )
                ]
            )
        )
    )
    fun userRoutes(): RouterFunction<ServerResponse> = router {
        "/users".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("", userHandler::getAllUsers)
                GET("/search", userHandler::searchUsers)
                GET("/username/{username}", userHandler::getUserByUsername)
                GET("/me", userHandler::getCurrentOAuth2User)
                GET("/me/provider", userHandler::getAuthProvider)
                GET("/email", userHandler::getUserByEmail)
                GET("/{id}", userHandler::getUserById)
                POST("", userHandler::createUser)
                POST("/ensure-from-me", userHandler::ensureUserFromMe)
                PUT("/{id}", userHandler::updateUser)
                DELETE("/{id}", userHandler::deleteUser)
            }
        }
    }
}