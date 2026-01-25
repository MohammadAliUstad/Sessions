package com.yugentech.sessions.models

import androidx.annotation.Keep

// Data model representing the user's profile information
@Keep
data class UserData(
    val userId: String = "",
    val name: String? = null,
    val email: String? = null,
    val avatarId: Int? = 0,
) {
    // Converts the user object to a map for database storage
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "name" to name,
        "email" to email,
        "avatarId" to avatarId
    )
}