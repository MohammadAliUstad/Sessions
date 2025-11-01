package com.yugentech.sessions.ui.auth.utils

data class SignInFormState(
    val email: String = "",
    val password: String = "",
    val emailError: String = "",
    val passwordError: String = ""
)