package com.yugentech.sessions.ui.auth.states

sealed class ForgotPasswordState {
    data object Idle : ForgotPasswordState()
    data object Loading : ForgotPasswordState()
    data object Success : ForgotPasswordState()
    data class Error(val message: String) : ForgotPasswordState()
}