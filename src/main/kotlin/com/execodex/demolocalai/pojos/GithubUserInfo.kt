package com.execodex.demolocalai.pojos

/**
 * POJO representing GitHub OAuth2 user information as returned by https://api.github.com/user
 * Fields follow GitHub's public user schema. Not all fields are always present depending on scopes.
 */
data class GithubUserInfo(
    val id: Long? = null,
    val nodeId: String? = null,
    val login: String? = null,
    val name: String? = null,
    val email: String? = null,
    val avatarUrl: String? = null,
    val htmlUrl: String? = null,
    val url: String? = null,
    val reposUrl: String? = null,
    val followersUrl: String? = null,
    val followingUrl: String? = null,
    val gistsUrl: String? = null,
    val starredUrl: String? = null,
    val subscriptionsUrl: String? = null,
    val organizationsUrl: String? = null,
    val eventsUrl: String? = null,
    val receivedEventsUrl: String? = null,

    val bio: String? = null,
    val company: String? = null,
    val blog: String? = null,
    val location: String? = null,
    val twitterUsername: String? = null,

    val publicRepos: Int? = null,
    val publicGists: Int? = null,
    val followers: Int? = null,
    val following: Int? = null,

    val createdAt: String? = null,
    val updatedAt: String? = null,

    val type: String? = null,
    val siteAdmin: Boolean? = null,

    // granted scopes as reported by Spring Security authorities (SCOPE_*)
    val scopes: List<String> = emptyList()
)
