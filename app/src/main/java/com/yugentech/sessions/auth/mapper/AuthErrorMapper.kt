package com.yugentech.sessions.auth.mapper

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
            "ERROR_WEAK_PASSWORD" -> "Password is too weak. Please use at least 6 characters."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "An account with this email already exists. Please sign in instead."
            "ERROR_INVALID_EMAIL" -> "Please enter a valid email address."

            "ERROR_USER_NOT_FOUND" -> "No account found with this email. Please check your email or sign up."
            "ERROR_WRONG_PASSWORD" -> "Incorrect password. Please try again."
            "ERROR_INVALID_CREDENTIAL" -> "Invalid email or password. Please check your credentials."
            "ERROR_USER_DISABLED" -> "This account has been disabled. Please contact support."
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "An account exists with this email but a different sign-in method."

            "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Please check your internet connection."
            "ERROR_TOO_MANY_REQUESTS" -> "Too many failed attempts. Please try again later."

            "ERROR_OPERATION_NOT_ALLOWED" -> "This sign-in method is not enabled. Contact support."
            "ERROR_REQUIRES_RECENT_LOGIN" -> "Please sign in again to complete this action."
            "ERROR_CREDENTIAL_ALREADY_IN_USE" -> "This credential is already associated with a different account."

            "ERROR_INVALID_CUSTOM_TOKEN" -> "Invalid authentication token. Please try again."
            "ERROR_CUSTOM_TOKEN_MISMATCH" -> "Authentication token mismatch. Please try again."

            "ERROR_USER_MISMATCH" -> "The credential does not correspond to the user."
            "ERROR_INVALID_USER_TOKEN", "ERROR_USER_TOKEN_EXPIRED" -> "Your session has expired. Please sign in again."

            else -> "Authentication failed. Please try again."
        }
    }

    // Handles generic system exceptions like network issues or timeouts
    private fun mapGeneralException(exception: Exception): String {
        val message = exception.message?.lowercase() ?: ""
        return when {
            message.contains("network") -> "Network error. Please check your connection."
            message.contains("timeout") -> "Request timed out. Please try again."
            message.contains("cancelled") -> "Operation was cancelled."
            message.contains("permission") -> "Permission denied. Check permissions and try again."
            else -> "Something went wrong. Please try again."
        }
    }

    // Specific mapper for Google Sign-In related errors
    fun mapGoogleSignInError(exception: Exception): String {
        val message = exception.message?.uppercase() ?: ""
        return when {
            message.contains("SIGN_IN_CANCELLED") || message.contains("CANCELLED") ->
                "Google Sign-In was cancelled."
            message.contains("SIGN_IN_FAILED") ->
                "Google Sign-In failed. Please try again."
            message.contains("NETWORK_ERROR") || message.contains("NETWORK") ->
                "Network error. Please check your connection."
            message.contains("SIGN_IN_CURRENTLY_IN_PROGRESS") ->
                "Sign-in is already in progress. Please wait."
            message.contains("API_NOT_CONNECTED") ->
                "Google Sign-In is not available. Try again later."
            message.contains("RESOLUTION_REQUIRED") ->
                "Google Sign-In requires additional setup. Please try again."
            message.contains("INVALID_ACCOUNT") ->
                "Invalid Google account. Please select a different account."
            message.contains("TIMEOUT") ->
                "Google Sign-In timed out. Please try again."
            else -> "Google Sign-In failed. Please try again or use email sign-in."
        }
    }
}