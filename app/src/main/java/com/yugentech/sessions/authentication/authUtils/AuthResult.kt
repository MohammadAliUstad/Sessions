package com.yugentech.sessions.authentication.authUtils

// Sealed class wrapper to handle success and error states for auth operations
sealed class AuthResult<out T> {
    data class Success<out T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}