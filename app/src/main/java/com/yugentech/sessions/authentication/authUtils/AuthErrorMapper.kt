package com.yugentech.sessions.authentication.authUtils

import com.google.firebase.auth.FirebaseAuthException

object AuthErrorMapper {

    fun mapFirebaseAuthError(exception: Exception): String {
        return when (exception) {
            is FirebaseAuthException -> mapFirebaseAuthException(exception)
            else -> mapGeneralException(exception)
        }
    }

    private fun mapFirebaseAuthException(exception: FirebaseAuthException): String {
        return when (exception.errorCode) {
            // Email/Password Sign Up Errors
            "ERROR_WEAK_PASSWORD" -> "Password is too weak. Please use at least 6 characters with a mix of letters and numbers."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "An account with this email already exists. Please sign in instead."
            "ERROR_INVALID_EMAIL" -> "Please enter a valid email address."

            // Email/Password Sign In Errors
            "ERROR_USER_NOT_FOUND" -> "No account found with this email. Please check your email or sign up."
            "ERROR_WRONG_PASSWORD" -> "Incorrect password. Please try again."
            "ERROR_INVALID_CREDENTIAL" -> "Invalid email or password. Please check your credentials and try again."
            "ERROR_USER_DISABLED" -> "This account has been disabled. Please contact support."
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "An account already exists with this email but with a different sign-in method."

            // Network and Rate Limiting
            "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Please check your internet connection and try again."
            "ERROR_TOO_MANY_REQUESTS" -> "Too many failed attempts. Please try again in a few minutes."

            // General Authentication Errors
            "ERROR_OPERATION_NOT_ALLOWED" -> "This sign-in method is not enabled. Please contact support."
            "ERROR_REQUIRES_RECENT_LOGIN" -> "Please sign in again to complete this action."
            "ERROR_CREDENTIAL_ALREADY_IN_USE" -> "This credential is already associated with a different user account."

            // Token Errors
            "ERROR_INVALID_CUSTOM_TOKEN" -> "Invalid authentication token. Please try again."
            "ERROR_CUSTOM_TOKEN_MISMATCH" -> "Authentication token mismatch. Please try again."

            // User Management Errors
            "ERROR_USER_MISMATCH" -> "The credential does not correspond to the user."
            "ERROR_INVALID_USER_TOKEN" -> "Your session has expired. Please sign in again."
            "ERROR_USER_TOKEN_EXPIRED" -> "Your session has expired. Please sign in again."

            // Default case
            else -> "Authentication failed. Please try again."
        }
    }

    private fun mapGeneralException(exception: Exception): String {
        val message = exception.message?.lowercase() ?: ""
        return when {
            message.contains("network") -> "Network error. Please check your internet connection and try again."
            message.contains("timeout") -> "Request timed out. Please try again."
            message.contains("cancelled") -> "Operation was cancelled. Please try again."
            message.contains("permission") -> "Permission denied. Please check your permissions and try again."
            else -> "Something went wrong. Please try again."
        }
    }

    fun mapGoogleSignInError(exception: Exception): String {
        val message = exception.message?.uppercase() ?: ""
        return when {
            message.contains("SIGN_IN_CANCELLED") || message.contains("CANCELLED") ->
                "Google Sign-In was cancelled."
            message.contains("SIGN_IN_FAILED") ->
                "Google Sign-In failed. Please try again."
            message.contains("NETWORK_ERROR") || message.contains("NETWORK") ->
                "Network error. Please check your internet connection and try again."
            message.contains("SIGN_IN_CURRENTLY_IN_PROGRESS") ->
                "Sign-in is already in progress. Please wait."
            message.contains("API_NOT_CONNECTED") ->
                "Google Sign-In is not available. Please try again later."
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