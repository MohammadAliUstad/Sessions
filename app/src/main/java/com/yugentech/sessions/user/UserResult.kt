package com.yugentech.sessions.user

// Sealed class wrapper for handling Success/Error/Loading states in user operations
sealed class UserResult<out T> {
    object Loading : UserResult<Nothing>()
    data class Success<T>(val data: T) : UserResult<T>()
    data class Error(val message: String, val exception: Throwable? = null) : UserResult<Nothing>()
}