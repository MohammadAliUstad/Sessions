package com.yugentech.sessions.ui.auth.state

import com.yugentech.sessions.utils.AppConstants.EMPTY

data class SignInFormState(
    val email: String = EMPTY,
    val password: String = EMPTY,
    val emailError: String = EMPTY,
    val passwordError: String = EMPTY
)