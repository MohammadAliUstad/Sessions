package com.yugentech.sessions.user.utils

import com.yugentech.sessions.models.UserData

data class UserState(
    val user: UserData? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)