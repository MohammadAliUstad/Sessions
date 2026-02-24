package com.yugentech.sessions.user.state

import com.yugentech.sessions.user.model.UserData

data class UserState(
    val user: UserData? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)