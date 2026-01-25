package com.yugentech.sessions.authentication.authUtils

// Sealed class representing the success or failure outcome of an auth operation
sealed class AuthResult<out T> {
    data class Success<out T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}