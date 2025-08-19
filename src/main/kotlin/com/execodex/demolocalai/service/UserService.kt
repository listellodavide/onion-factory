package com.execodex.demolocalai.service

import com.execodex.demolocalai.entities.User
import com.execodex.demolocalai.repositories.UserRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.security.SecureRandom
import java.util.Base64

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
                    pictureUrl = user.pictureUrl,
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

    /**
     * Ensure a user exists for the given Google profile. If not, create it.
     * Username is derived from display name or email local-part and appended with a short random suffix to avoid collisions.
     * Password is a random opaque value as placeholder.
     */
    fun ensureUserFromGoogleProfile(email: String, displayName: String?, pictureUrl: String?): Mono<User> {
        val safeEmail = email.trim().lowercase()
        return userRepository.findByEmail(safeEmail)
            .switchIfEmpty(createUserFromGoogle(safeEmail, displayName, pictureUrl))
    }

    private fun createUserFromGoogle(email: String, displayName: String?, pictureUrl: String?): Mono<User> {
        val base = (displayName?.ifBlank { null }
            ?: email.substringBefore('@'))
            .lowercase()
            .replace("[^a-z0-9]+".toRegex(), ".")
            .trim('.')
        fun randomSuffix(): String {
            val random = SecureRandom()
            val bytes = ByteArray(2) // 16 bits
            random.nextBytes(bytes)
            // base64 without padding, alnum-ish
            return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
                .lowercase()
        }
        val password = generateOpaquePassword()
        fun attemptSave(): Mono<User> {
            val candidate = "$base-${randomSuffix()}"
            val user = User(
                username = candidate,
                password = password,
                email = email,
                pictureUrl = pictureUrl
            )
            return userRepository.save(user)
        }
        // Try a few times in the rare case of username collision
        return attemptSave()
            .onErrorResume(DataIntegrityViolationException::class.java) { attemptSave() }
            .onErrorResume(DataIntegrityViolationException::class.java) { attemptSave() }
    }

    private fun generateOpaquePassword(): String {
        val random = SecureRandom()
        val bytes = ByteArray(24)
        random.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}