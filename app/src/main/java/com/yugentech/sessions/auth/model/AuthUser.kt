package com.yugentech.sessions.auth.model

/**
 * Whoever is using the app: a Firebase account, or a guest who exists only on this phone
 * ([isGuest], with no name or email).
 */
data class AuthUser(
    val uid: String,
    val isGuest: Boolean,
    val displayName: String? = null,
    val email: String? = null
)
