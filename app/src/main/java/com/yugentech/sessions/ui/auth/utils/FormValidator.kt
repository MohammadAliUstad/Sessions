package com.yugentech.sessions.ui.auth.utils

import android.util.Patterns

object FormValidator {

    fun validateName(name: String): String {
        return when {
            name.isBlank() -> "Name cannot be empty"
            name.length < 2 -> "Name must be at least 2 characters"
            else -> ""
        }
    }

    fun validateEmail(email: String): String {
        return when {
            email.isBlank() -> "Email cannot be empty"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Please enter a valid email"
            else -> ""
        }
    }

    fun validatePassword(password: String): String {
        return when {
            password.isBlank() -> "Password cannot be empty"
            password.length < 8 -> "Password must be at least 8 characters"
            !password.any { it.isDigit() } -> "Password must contain at least one number"
            !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
            !password.any { it.isLowerCase() } -> "Password must contain at least one lowercase letter"
            else -> ""
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String {
        return when {
            confirmPassword.isBlank() -> "Please confirm your password"
            password != confirmPassword -> "Passwords do not match"
            else -> ""
        }
    }

    fun validateSignInForm(
        email: String,
        password: String,
        onEmailError: (String) -> Unit,
        onPasswordError: (String) -> Unit
    ): Boolean {
        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)

        onEmailError(emailError)
        onPasswordError(passwordError)

        return emailError.isEmpty() && passwordError.isEmpty()
    }

    fun validateSignUpForm(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        onNameError: (String) -> Unit,
        onEmailError: (String) -> Unit,
        onPasswordError: (String) -> Unit,
        onConfirmPasswordError: (String) -> Unit
    ): Boolean {
        val nameError = validateName(name)
        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)
        val confirmPasswordError = validateConfirmPassword(password, confirmPassword)

        onNameError(nameError)
        onEmailError(emailError)
        onPasswordError(passwordError)
        onConfirmPasswordError(confirmPasswordError)

        return nameError.isEmpty() && emailError.isEmpty() &&
               passwordError.isEmpty() && confirmPasswordError.isEmpty()
    }
}