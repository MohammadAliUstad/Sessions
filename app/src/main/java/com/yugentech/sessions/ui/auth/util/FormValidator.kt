package com.yugentech.sessions.ui.auth.util

import android.content.res.Resources
import android.util.Patterns
import com.yugentech.sessions.R
import com.yugentech.sessions.utils.AppConstants.EMPTY

/** Form checks; each returns the error to show (from [Resources], so it's in the app's language) or empty. */
object FormValidator {

    fun validateName(res: Resources, name: String): String {
        return when {
            name.isBlank() -> res.getString(R.string.validation_name_empty)
            name.length < 2 -> res.getString(R.string.validation_name_too_short)
            else -> EMPTY
        }
    }

    fun validateEmail(res: Resources, email: String): String {
        return when {
            email.isBlank() -> res.getString(R.string.validation_email_empty)
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                res.getString(R.string.validation_email_invalid)

            else -> EMPTY
        }
    }

    fun validatePassword(res: Resources, password: String): String {
        return when {
            password.isBlank() -> res.getString(R.string.validation_password_empty)
            password.length < 8 -> res.getString(R.string.validation_password_too_short)
            !password.any { it.isDigit() } -> res.getString(R.string.validation_password_needs_number)
            !password.any { it.isUpperCase() } -> res.getString(R.string.validation_password_needs_uppercase)
            !password.any { it.isLowerCase() } -> res.getString(R.string.validation_password_needs_lowercase)
            else -> EMPTY
        }
    }

    fun validateSignInForm(
        res: Resources,
        email: String,
        password: String,
        onEmailError: (String) -> Unit,
        onPasswordError: (String) -> Unit
    ): Boolean {
        val emailError = validateEmail(res, email)
        val passwordError = validatePassword(res, password)

        onEmailError(emailError)
        onPasswordError(passwordError)

        return emailError.isEmpty() && passwordError.isEmpty()
    }

    fun validateSignUpForm(
        res: Resources,
        name: String,
        email: String,
        password: String,
        onNameError: (String) -> Unit,
        onEmailError: (String) -> Unit,
        onPasswordError: (String) -> Unit
    ): Boolean {
        val nameError = validateName(res, name)
        val emailError = validateEmail(res, email)
        val passwordError = validatePassword(res, password)

        onNameError(nameError)
        onEmailError(emailError)
        onPasswordError(passwordError)

        return nameError.isEmpty() && emailError.isEmpty() && passwordError.isEmpty()
    }
}