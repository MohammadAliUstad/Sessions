package com.yugentech.sessions.user.model

import androidx.annotation.Keep

@Keep
data class UserData(
    val userId: String = "",
    val name: String? = null,
    val email: String? = null,
    val avatarId: Int? = 0,
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "name" to name,
        "email" to email,
        "avatarId" to avatarId
    )
}