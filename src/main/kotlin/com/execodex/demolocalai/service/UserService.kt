package com.execodex.demolocalai.service

import com.execodex.demolocalai.entities.User
import com.execodex.demolocalai.repositories.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service for managing users.
 */
@Service
class UserService(private val userRepository: UserRepository) {

    /**
     * Get all users.
     *
     * @return a Flux of all users
     */
    fun getAllUsers(): Flux<User> = userRepository.findAll()

    /**
     * Get a user by its ID.
     *
     * @param id the ID of the user
     * @return a Mono containing the user if found
     */
    fun getUserById(id: Long): Mono<User> = userRepository.findById(id)

    /**
     * Get a user by username.
     *
     * @param username the username to search for
     * @return a Mono containing the user if found
     */
    fun getUserByUsername(username: String): Mono<User> = userRepository.findByUsername(username)

    /**
     * Get a user by email.
     *
     * @param email the email to search for
     * @return a Mono containing the user if found
     */
    fun getUserByEmail(email: String): Mono<User> = userRepository.findByEmail(email)

    /**
     * Create a new user.
     *
     * @param user the user to create
     * @return a Mono containing the created user
     */
    fun createUser(user: User): Mono<User> = userRepository.save(user)

    /**
     * Update an existing user.
     *
     * @param id the ID of the user to update
     * @param user the updated user data
     * @return a Mono containing the updated user if found
     */
    fun updateUser(id: Long, user: User): Mono<User> {
        return userRepository.findById(id)
            .flatMap { existingUser ->
                val updatedUser = User(
                    id = existingUser.id,
                    username = user.username,
                    password = user.password,
                    email = user.email,
                    createdAt = existingUser.createdAt
                )
                userRepository.save(updatedUser)
            }
    }

    /**
     * Delete a user by its ID.
     *
     * @param id the ID of the user to delete
     * @return a Mono completing when the user is deleted
     */
    fun deleteUser(id: Long): Mono<Void> = userRepository.deleteById(id)

    /**
     * Find users by username pattern.
     *
     * @param usernamePattern the pattern to match against usernames
     * @return a Flux of users matching the pattern
     */
    fun findUsersByUsernamePattern(usernamePattern: String): Flux<User> = 
        userRepository.findByUsernameContainingIgnoreCase(usernamePattern)
}