package com.yugentech.sessions.session.sessionUtils

sealed class SessionResult<out T> {
    data class Success<out T>(val data: T) : SessionResult<T>()
    data class Error(val message: String) : SessionResult<Nothing>()
}