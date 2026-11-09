package com.yugentech.sessions.auth.mapper

import com.yugentech.sessions.localization.AppStrings
import com.yugentech.sessions.R
import com.google.firebase.auth.FirebaseAuthException

// Utility to translate technical exceptions into user-friendly error messages
object AuthErrorMapper {

    // Main entry point to map any exception to a readable string
    fun mapFirebaseAuthError(exception: Exception): String {
        return when (exception) {
            is FirebaseAuthException -> mapFirebaseAuthException(exception)
            else -> mapGeneralException(exception)
        }
    }

    // Handles specific Firebase authentication error codes
    private fun mapFirebaseAuthException(exception: FirebaseAuthException): String {
        return when (exception.errorCode) {
            "ERROR_WEAK_PASSWORD" -> AppStrings.get(R.string.auth_error_weak_password)
            "ERROR_EMAIL_ALREADY_IN_USE" -> AppStrings.get(R.string.auth_error_email_in_use)
            "ERROR_INVALID_EMAIL" -> AppStrings.get(R.string.auth_error_invalid_email)

            "ERROR_USER_NOT_FOUND" -> AppStrings.get(R.string.auth_error_user_not_found)
            "ERROR_WRONG_PASSWORD" -> AppStrings.get(R.string.auth_error_wrong_password)
            "ERROR_INVALID_CREDENTIAL" -> AppStrings.get(R.string.auth_error_invalid_credential)
            "ERROR_USER_DISABLED" -> AppStrings.get(R.string.auth_error_user_disabled)
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> AppStrings.get(R.string.auth_error_different_credential)

            "ERROR_NETWORK_REQUEST_FAILED" -> AppStrings.get(R.string.auth_error_network)
            "ERROR_TOO_MANY_REQUESTS" -> AppStrings.get(R.string.auth_error_too_many_requests)

            "ERROR_OPERATION_NOT_ALLOWED" -> AppStrings.get(R.string.auth_error_not_allowed)
            "ERROR_REQUIRES_RECENT_LOGIN" -> AppStrings.get(R.string.auth_error_recent_login)
            "ERROR_CREDENTIAL_ALREADY_IN_USE" -> AppStrings.get(R.string.auth_error_credential_in_use)

            "ERROR_INVALID_CUSTOM_TOKEN" -> AppStrings.get(R.string.auth_error_invalid_token)
            "ERROR_CUSTOM_TOKEN_MISMATCH" -> AppStrings.get(R.string.auth_error_token_mismatch)

            "ERROR_USER_MISMATCH" -> AppStrings.get(R.string.auth_error_user_mismatch)
            "ERROR_INVALID_USER_TOKEN", "ERROR_USER_TOKEN_EXPIRED" -> AppStrings.get(R.string.auth_error_session_expired)

            else -> AppStrings.get(R.string.auth_error_generic)
        }
    }

    // Handles generic system exceptions like network issues or timeouts
    private fun mapGeneralException(exception: Exception): String {
        val message = exception.message?.lowercase() ?: ""
        return when {
            message.contains("network") -> AppStrings.get(R.string.error_network)
            message.contains("timeout") -> AppStrings.get(R.string.error_timeout)
            message.contains("cancelled") -> AppStrings.get(R.string.error_cancelled)
            message.contains("permission") -> AppStrings.get(R.string.error_permission)
            else -> AppStrings.get(R.string.error_generic)
        }
    }

    // Specific mapper for Google Sign-In related errors
    fun mapGoogleSignInError(exception: Exception): String {
        val message = exception.message?.uppercase() ?: ""
        return when {
            message.contains("SIGN_IN_CANCELLED") || message.contains("CANCELLED") ->
                AppStrings.get(R.string.google_error_cancelled)
            message.contains("SIGN_IN_FAILED") ->
                AppStrings.get(R.string.google_error_failed)
            message.contains("NETWORK_ERROR") || message.contains("NETWORK") ->
                AppStrings.get(R.string.error_network)
            message.contains("SIGN_IN_CURRENTLY_IN_PROGRESS") ->
                AppStrings.get(R.string.google_error_in_progress)
            message.contains("API_NOT_CONNECTED") ->
                AppStrings.get(R.string.google_error_unavailable)
            message.contains("RESOLUTION_REQUIRED") ->
                AppStrings.get(R.string.google_error_resolution)
            message.contains("INVALID_ACCOUNT") ->
                AppStrings.get(R.string.google_error_invalid_account)
            message.contains("TIMEOUT") ->
                AppStrings.get(R.string.google_error_timeout)
            else -> AppStrings.get(R.string.google_error_fallback)
        }
    }
}