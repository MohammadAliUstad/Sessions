package com.yugentech.sessions // Use your actual package name

object Validator {
    
    fun validatePassword(password: String): Boolean {
        // Logic: Password must be at least 6 characters long and not empty
        if (password.isEmpty()) return false
        if (password.length < 6) return false
        return true
    }
}