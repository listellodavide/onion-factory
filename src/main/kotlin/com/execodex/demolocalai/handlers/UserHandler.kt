package com.execodex.demolocalai.handlers

import com.execodex.demolocalai.entities.User
import com.execodex.demolocalai.handlers.errors.UserErrorHandler
import com.execodex.demolocalai.pojos.GoogleUserInfo
import com.execodex.demolocalai.pojos.GithubUserInfo
import com.execodex.demolocalai.pojos.UserResponse
import com.execodex.demolocalai.pojos.AuthProviderResponse
import com.execodex.demolocalai.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
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
class UserHandler(
    private val userService: UserService,
    private val userErrorHandler: UserErrorHandler
) {
    private val logger = LoggerFactory.getLogger(UserHandler::class.java)

    /**
     * Extract attributes map from a principal, supporting OIDC and generic OAuth2 users.
     */
    private fun extractAttributes(principal: Any?): Map<String, Any?> = when (principal) {
        is OidcUser -> principal.claims
        is DefaultOAuth2User -> principal.attributes
        else -> emptyMap()
    }

    /**
     * Detect the OAuth2/OIDC provider from the Authentication or attributes.
     * Returns lowercase provider id (e.g., "google", "github") or "unknown" if it cannot be determined.
     */
    private fun detectAuthProvider(auth: Authentication, attributes: Map<String, Any?>): String {
        return when (auth) {
            is OAuth2AuthenticationToken -> auth.authorizedClientRegistrationId ?: "unknown"
            else -> {
                val iss = attributes["iss"]?.toString()?.lowercase()
                when {
                    iss?.contains("google") == true -> "google"
                    attributes.containsKey("avatar_url") || iss?.contains("github") == true -> "github"
                    else -> "unknown"
                }
            }
        }
    }

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
            .onErrorResume { error ->
                logger.error("Error creating user: ${error.message}", error)
                userErrorHandler.handleError(error)
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

    /**
     * Returns the current authentication provider (e.g., google, github).
     * If not authenticated, returns 401 Unauthorized.
     */
    fun getAuthProvider(request: ServerRequest): Mono<ServerResponse> {
        return request.principal()
            .cast(Authentication::class.java)
            .flatMap { auth ->
                val principal = auth.principal
                val attributes = extractAttributes(principal)
                val provider = detectAuthProvider(auth, attributes)
                logger.info("OAuth2 login provider detected: {}", provider)
                ServerResponse.ok().bodyValue(AuthProviderResponse(provider = provider))
            }
            .switchIfEmpty(ServerResponse.status(401).build())
    }

    /**
     * Returns the current Google-authenticated user's info based on OIDC claims.
     * If not authenticated, returns 401 Unauthorized.
     */
    fun getCurrentOAuth2User(request: ServerRequest): Mono<ServerResponse> {
        return request.principal()
            .cast(Authentication::class.java)
            .flatMap { auth ->
                val principal = auth.principal
                logger.info("Principal: $principal")
                when (principal) {is OidcUser -> logger.info("OIDC User: ${principal.idToken.claims}")}
                when (principal) {is DefaultOAuth2User -> logger.info("OAuth2 User: ${principal.attributes}")}

                val attributes = extractAttributes(principal)

                // Determine provider (google/github) and log it
                val provider: String = detectAuthProvider(auth, attributes)
                logger.info("OAuth2 login provider detected: {}", provider)

                val scopes: List<String> = auth.authorities
                    .map(GrantedAuthority::getAuthority)
                    .filter { it.startsWith("SCOPE_") }
                    .map { it.removePrefix("SCOPE_") }

                val info: Any = when (provider.lowercase()) {
                    "google" -> {
                        GoogleUserInfo(
                            subject = attributes["sub"] as? String,
                            name = attributes["name"] as? String,
                            givenName = (attributes["given_name"] ?: attributes["givenName"]) as? String,
                            familyName = (attributes["family_name"] ?: attributes["familyName"]) as? String,
                            picture = attributes["picture"] as? String,
                            locale = attributes["locale"] as? String,
                            email = attributes["email"] as? String,
                            emailVerified = (attributes["email_verified"] as? Boolean)
                                ?: (attributes["emailVerified"] as? Boolean),
                            scopes = scopes
                        )
                    }
                    "github" -> {
                        val idValue = attributes["id"]
                        val idLong: Long? = when (idValue) {
                            is Number -> idValue.toLong()
                            is String -> idValue.toLongOrNull()
                            else -> null
                        }
                        GithubUserInfo(
                            id = idLong,
                            nodeId = attributes["node_id"] as? String,
                            login = attributes["login"] as? String,
                            name = attributes["name"] as? String,
                            email = attributes["email"] as? String,
                            avatarUrl = attributes["avatar_url"] as? String,
                            htmlUrl = attributes["html_url"] as? String,
                            url = attributes["url"] as? String,
                            reposUrl = attributes["repos_url"] as? String,
                            followersUrl = attributes["followers_url"] as? String,
                            followingUrl = attributes["following_url"] as? String,
                            gistsUrl = attributes["gists_url"] as? String,
                            starredUrl = attributes["starred_url"] as? String,
                            subscriptionsUrl = attributes["subscriptions_url"] as? String,
                            organizationsUrl = attributes["organizations_url"] as? String,
                            eventsUrl = attributes["events_url"] as? String,
                            receivedEventsUrl = attributes["received_events_url"] as? String,
                            bio = attributes["bio"] as? String,
                            company = attributes["company"] as? String,
                            blog = attributes["blog"] as? String,
                            location = attributes["location"] as? String,
                            twitterUsername = attributes["twitter_username"] as? String,
                            publicRepos = (attributes["public_repos"] as? Number)?.toInt(),
                            publicGists = (attributes["public_gists"] as? Number)?.toInt(),
                            followers = (attributes["followers"] as? Number)?.toInt(),
                            following = (attributes["following"] as? Number)?.toInt(),
                            createdAt = attributes["created_at"] as? String,
                            updatedAt = attributes["updated_at"] as? String,
                            type = attributes["type"] as? String,
                            siteAdmin = attributes["site_admin"] as? Boolean,
                            scopes = scopes
                        )
                    }
                    else -> {
                        mapOf(
                            "provider" to provider,
                            "attributes" to attributes,
                            "scopes" to scopes
                        )
                    }
                }

                ServerResponse.ok().bodyValue(info)
            }
            .switchIfEmpty(ServerResponse.status(401).build())
    }

    /**
     * Ensures the user is authenticated and returns the application user by email from the principal.
     * - 401 if not authenticated
     * - 400 if email not present in principal
     * - 404 if no user exists with that email
     */
    fun ensureUserFromMe(request: ServerRequest): Mono<ServerResponse> {
        return request.principal()
            .cast(Authentication::class.java)
            .flatMap { auth ->
                val principal = auth.principal
                val attributes: Map<String, Any?> = when (principal) {
                    is OidcUser -> principal.claims
                    is DefaultOAuth2User -> principal.attributes
                    else -> emptyMap()
                }

                val email = (attributes["email"] as? String)?.trim()?.lowercase()
                if (email.isNullOrBlank()) {
                    return@flatMap ServerResponse.badRequest()
                        .bodyValue(mapOf("error" to "Email not found in principal attributes"))
                }

                val displayName = attributes["name"] as? String
                val pictureUrl = attributes["picture"] as? String

                userService.ensureUserFromGoogleProfile(email, displayName, pictureUrl)
                    .flatMap { user ->
                        val dto = UserResponse(
                            id = user.id,
                            username = user.username,
                            email = user.email,
                            createdAt = user.createdAt,
                            pictureUrl = user.pictureUrl
                        )
                        ServerResponse.ok().bodyValue(dto)
                    }
            }
            .switchIfEmpty(
                ServerResponse.status(401)
                    .bodyValue(mapOf("error" to "Unauthenticated"))
            )
    }
}