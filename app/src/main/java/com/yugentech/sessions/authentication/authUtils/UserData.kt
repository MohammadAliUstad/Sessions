package com.yugentech.sessions.authentication.authUtils

data class UserData(
    val userId: String? = null,
    val username: String? = null,
    val email: String? = null,
    val profileAvatarId: String? = "default"
)