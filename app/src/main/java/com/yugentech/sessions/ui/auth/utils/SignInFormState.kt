package com.yugentech.sessions.ui.auth.utils

import com.yugentech.sessions.theme.tokens.dimensions.AppConstants

data class SignInFormState(
    val email: String = AppConstants.EMPTY_STRING,
    val password: String = AppConstants.EMPTY_STRING,
    val emailError: String = AppConstants.EMPTY_STRING,
    val passwordError: String = AppConstants.EMPTY_STRING
)