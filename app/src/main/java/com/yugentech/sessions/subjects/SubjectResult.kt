package com.yugentech.sessions.subjects

// Generic wrapper for handling Success/Error states in Subject operations
sealed class SubjectResult<out T> {
    data class Success<out T>(val data: T) : SubjectResult<T>()
    data class Error(val message: String) : SubjectResult<Nothing>()
}