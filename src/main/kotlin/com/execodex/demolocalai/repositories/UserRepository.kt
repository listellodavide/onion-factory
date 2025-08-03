package com.execodex.demolocalai.repositories

import com.execodex.demolocalai.entities.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Reactive repository for User entities.
 */
@Repository
interface UserRepository : ReactiveCrudRepository<User, Long> {
    /**
     * Find a user by username.
     *
     * @param username the username to search for
     * @return a Mono containing the user if found
     */
    fun findByUsername(username: String): Mono<User>
    
    /**
     * Find a user by email.
     *
     * @param email the email to search for
     * @return a Mono containing the user if found
     */
    fun findByEmail(email: String): Mono<User>
    
    /**
     * Find users by partial username match.
     *
     * @param usernamePattern the pattern to match against usernames
     * @return a Flux of users matching the pattern
     */
    fun findByUsernameContainingIgnoreCase(usernamePattern: String): Flux<User>
}