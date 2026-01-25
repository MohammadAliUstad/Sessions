package com.yugentech.sessions.authentication.authUtils

import android.app.PendingIntent
import com.yugentech.sessions.models.UserData

// Data class holding the current state of the authentication UI
data class AuthState(
    val isLoading: Boolean = false,
    val userId: String? = null,
    val error: String? = null,
    val intent: PendingIntent? = null,
    val userData: UserData? = null,
    val isUserLoggedIn: Boolean = true
)