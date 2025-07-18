package com.yugentech.sessions.authentication.authUtils

import android.app.PendingIntent

data class AuthState(
    val isLoading: Boolean = false,
    val userId: String? = null,
    val error: String? = null,
    val pendingIntent: PendingIntent? = null,
    val userData: UserData? = null,
    val isUserLoggedIn: Boolean = true
)