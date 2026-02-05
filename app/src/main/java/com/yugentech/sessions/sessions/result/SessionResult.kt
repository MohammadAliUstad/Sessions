package com.yugentech.sessions.sessions.result

// A wrapper to handle success data or error messages easily
sealed class SessionResult<out T> {
    data class Success<out T>(val data: T) : SessionResult<T>()
    data class Error(val message: String) : SessionResult<Nothing>()
}