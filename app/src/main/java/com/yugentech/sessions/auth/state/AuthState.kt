package com.yugentech.sessions.auth.state

import android.app.PendingIntent
import com.yugentech.sessions.user.model.UserData

// Data class holding the current state of the authentication UI
data class AuthState(
    val isInitializing: Boolean = true,
    val isLoading: Boolean = false,
    val userId: String? = null,
    val error: String? = null,
    val intent: PendingIntent? = null,
    val userData: UserData? = null,
    val isUserLoggedIn: Boolean = false
)