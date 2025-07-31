package com.yugentech.sessions.status

sealed class StatusResult<out T> {
    data class Success<out T>(val data: T) : StatusResult<T>()
    data class Error(val exception: Throwable) : StatusResult<Nothing>()
}