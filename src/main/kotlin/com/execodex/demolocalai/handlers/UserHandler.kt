package com.execodex.demolocalai.handlers

import com.execodex.demolocalai.entities.User
import com.execodex.demolocalai.service.UserService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import java.net.URI

/**
 * Handler for user-related HTTP requests.
 */
@Component
class UserHandler(private val userService: UserService) {

    /**
     * Get all users.
     *
     * @param request the server request
     * @return a server response containing all users
     */
    fun getAllUsers(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok()
            .body(userService.getAllUsers(), User::class.java)
    }

    /**
     * Get a user by its ID.
     *
     * @param request the server request containing the user ID
     * @return a server response containing the user if found
     */
    fun getUserById(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLong()
        return userService.getUserById(id)
            .flatMap { user -> ServerResponse.ok().bodyValue(user) }
            .switchIfEmpty(ServerResponse.notFound().build())
    }

    /**
     * Create a new user.
     *
     * @param request the server request containing the user data
     * @return a server response containing the created user
     */
    fun createUser(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono<User>()
            .flatMap { user -> userService.createUser(user) }
            .flatMap { savedUser ->
                ServerResponse.created(URI.create("/users/${savedUser.id}"))
                    .bodyValue(savedUser)
            }
    }

    /**
     * Update an existing user.
     *
     * @param request the server request containing the user ID and updated data
     * @return a server response containing the updated user if found
     */
    fun updateUser(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLong()
        return request.bodyToMono<User>()
            .flatMap { user -> userService.updateUser(id, user) }
            .flatMap { updatedUser -> ServerResponse.ok().bodyValue(updatedUser) }
            .switchIfEmpty(ServerResponse.notFound().build())
    }

    /**
     * Delete a user by its ID.
     *
     * @param request the server request containing the user ID
     * @return a server response with no content if successful
     */
    fun deleteUser(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLong()
        return userService.deleteUser(id)
            .then(ServerResponse.noContent().build())
    }

    /**
     * Search for users by username pattern.
     *
     * @param request the server request containing the search query
     * @return a server response containing the matching users
     */
    fun searchUsers(request: ServerRequest): Mono<ServerResponse> {
        val query = request.queryParam("username").orElse("")
        return ServerResponse.ok()
            .body(userService.findUsersByUsernamePattern(query), User::class.java)
    }

    /**
     * Get a user by username.
     *
     * @param request the server request containing the username
     * @return a server response containing the user if found
     */
    fun getUserByUsername(request: ServerRequest): Mono<ServerResponse> {
        val username = request.pathVariable("username")
        return userService.getUserByUsername(username)
            .flatMap { user -> ServerResponse.ok().bodyValue(user) }
            .switchIfEmpty(ServerResponse.notFound().build())
    }

    /**
     * Get a user by email.
     *
     * @param request the server request containing the email
     * @return a server response containing the user if found
     */
    fun getUserByEmail(request: ServerRequest): Mono<ServerResponse> {
        val email = request.queryParam("email").orElse("")
        return userService.getUserByEmail(email)
            .flatMap { user -> ServerResponse.ok().bodyValue(user) }
            .switchIfEmpty(ServerResponse.notFound().build())
    }
}