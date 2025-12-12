package com.yugentech.sessions.models

import androidx.annotation.Keep

// Represents the core user profile information synced across devices
@Keep
data class UserData(
    val userId: String = "",
    val name: String? = null,
    val email: String? = null,
    val avatarId: Int? = 0,
) {
    // Serializes user profile for Firestore persistence
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "name" to name,
        "email" to email,
        "avatarId" to avatarId
    )
}