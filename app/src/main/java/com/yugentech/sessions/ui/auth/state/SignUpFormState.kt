package com.yugentech.sessions.ui.auth.state

import com.yugentech.sessions.utils.AppConstants.EMPTY

data class SignUpFormState(
    val name: String = EMPTY,
    val email: String = EMPTY,
    val password: String = EMPTY,
    val confirmPassword: String = EMPTY,
    val nameError: String = EMPTY,
    val emailError: String = EMPTY,
    val passwordError: String = EMPTY,
    val confirmPasswordError: String = EMPTY
)