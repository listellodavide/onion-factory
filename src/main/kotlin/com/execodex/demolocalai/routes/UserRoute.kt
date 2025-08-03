package com.execodex.demolocalai.routes

import com.execodex.demolocalai.handlers.UserHandler
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
    fun userRoutes(): RouterFunction<ServerResponse> = router {
        "/users".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("", userHandler::getAllUsers)
                GET("/{id}", userHandler::getUserById)
                POST("", userHandler::createUser)
                PUT("/{id}", userHandler::updateUser)
                DELETE("/{id}", userHandler::deleteUser)
                GET("/search", userHandler::searchUsers)
                GET("/username/{username}", userHandler::getUserByUsername)
                GET("/email", userHandler::getUserByEmail)
            }
        }
    }
}