package com.yugentech.sessions.auth.repository

import android.content.Intent
import com.google.firebase.auth.FirebaseUser
import com.yugentech.sessions.auth.guest.GuestSession
import com.yugentech.sessions.auth.model.AuthUser
import com.yugentech.sessions.auth.service.AuthService
import com.yugentech.sessions.auth.result.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import timber.log.Timber

// Implementation that delegates calls to the underlying AuthService, plus the local guest
class AuthRepositoryImpl(
    private val authService: AuthService,
    private val guestSession: GuestSession
) : AuthRepository {

    // A Firebase account wins; otherwise the local guest, if there is one
    override val authState: Flow<AuthUser?> =
        combine(authService.authStateFlow, guestSession.guestId) { user, guestId ->
            when {
                // An anonymous Firebase guest from an earlier build: keep its ID as the local
                // guest ID, so its data stays, and sign it out of Firebase
                user != null && user.isAnonymous -> {
                    Timber.i("Moving an anonymous Firebase guest to a local guest")
                    guestSession.adopt(user.uid)
                    authService.signOut()
                    AuthUser(uid = user.uid, isGuest = true)
                }

                user != null -> AuthUser(
                    uid = user.uid,
                    isGuest = false,
                    displayName = user.displayName,
                    email = user.email
                )

                guestId != null -> AuthUser(uid = guestId, isGuest = true)
                else -> null
            }
        }

    // The signed-in account's ID, or the local guest's
    override val currentUser: String?
        get() = authService.currentUser?.uid ?: guestSession.guestId.value

    // Registers a new user with email and password
    override suspend fun signUp(name: String, email: String, password: String): AuthResult<FirebaseUser> {
        Timber.i("Sign up requested")
        return authService.signUp(name, email, password)
    }

    // Authenticates an existing user
    override suspend fun signIn(email: String, password: String): AuthResult<FirebaseUser> {
        Timber.i("Sign in requested")
        return authService.signIn(email, password)
    }

    // Triggers a password recovery email for the user
    override suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        Timber.i("Password reset requested")
        return authService.sendPasswordResetEmail(email)
    }

    // Logs the current user out of the application
    override fun signOut() {
        Timber.i("Signing out current user")
        authService.signOut()
    }

    // Prepares the intent required to launch the Google Sign-In flow
    override suspend fun getGoogleSignInIntent(webClientId: String): AuthResult<android.app.PendingIntent> {
        Timber.d("Requesting Google Sign-In Intent")
        return authService.getGoogleSignInIntent(webClientId)
    }

    // Processes the result returned from the Google Sign-In activity
    override suspend fun handleGoogleSignInResult(data: Intent?): AuthResult<FirebaseUser> {
        Timber.i("Handling Google Sign-In result")
        return authService.handleGoogleSignInResult(data)
    }

    override val isGuest: Boolean
        get() = authService.currentUser == null && guestSession.guestId.value != null

    override fun startGuest() {
        Timber.i("Guest mode started")
        guestSession.start()
    }

    override fun endGuest() {
        Timber.i("Guest mode ended")
        guestSession.end()
    }
}
