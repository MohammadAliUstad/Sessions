package com.yugentech.sessions.ui.auth.signUpScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.auth.viewmodel.AuthViewModel
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.auth.components.AuthScaffold
import com.yugentech.sessions.ui.auth.components.AuthSwitchLink
import com.yugentech.sessions.ui.auth.components.WeekStreak
import com.yugentech.sessions.ui.auth.components.forms.SignUpForm
import com.yugentech.sessions.ui.dash.mainScreen.components.ToastMessage

/** A new focus habit, under a week of focus building up into a streak. */
@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onSignUp: (name: String, email: String, password: String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    AuthScaffold(
        header = { WeekStreak() },
        formTitle = "Sign up",
        title = "Build a focus habit",
        subtitle = "A calm timer, gentle nudges, and a record of every minute you focus.",
        overlay = {
            Box(modifier = Modifier.fillMaxSize()) {
                ToastMessage(
                    message = authState.error,
                    onDismiss = { authViewModel.clearError() },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .zIndex(1f)
                )
            }
        }
    ) {
        SignUpForm(
            isLoading = authState.isLoading,
            onSignUp = onSignUp,
            onGoogleSignIn = onGoogleSignIn,
            onClearError = { authViewModel.clearError() }
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

        AuthSwitchLink(
            prompt = "Already focusing with us?",
            action = "Sign in",
            onClick = onNavigateToSignIn
        )
    }
}
