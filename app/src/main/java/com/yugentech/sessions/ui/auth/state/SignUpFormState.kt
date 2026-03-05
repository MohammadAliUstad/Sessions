package com.yugentech.sessions.ui.auth.state

import com.yugentech.sessions.utils.AppConstants.EMPTY_STRING

data class SignUpFormState(
    val name: String = EMPTY_STRING,
    val email: String = EMPTY_STRING,
    val password: String = EMPTY_STRING,
    val confirmPassword: String = EMPTY_STRING,
    val nameError: String = EMPTY_STRING,
    val emailError: String = EMPTY_STRING,
    val passwordError: String = EMPTY_STRING,
    val confirmPasswordError: String = EMPTY_STRING
)