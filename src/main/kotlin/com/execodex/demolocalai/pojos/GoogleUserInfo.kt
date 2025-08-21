package com.execodex.demolocalai.pojos

/**
 * POJO representing Google OAuth2/OIDC user information.
 * Fields correspond to common claims available with scopes: openid, email, profile.
 */
data class GoogleUserInfo(
    // openid scope
    val subject: String? = null,

    // profile scope
    val name: String? = null,
    val givenName: String? = null,
    val familyName: String? = null,
    val picture: String? = null,
    val locale: String? = null,

    // email scope
    val email: String? = null,
    val emailVerified: Boolean? = null,

    // granted scopes as reported by Spring Security authorities (SCOPE_*)
    val scopes: List<String> = emptyList()
)