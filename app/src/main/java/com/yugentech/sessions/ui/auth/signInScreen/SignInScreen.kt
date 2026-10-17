package com.yugentech.sessions.ui.auth.signInScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.R
import com.yugentech.sessions.auth.viewmodel.AuthViewModel
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.auth.components.AuthScaffold
import com.yugentech.sessions.ui.auth.components.AuthSwitchLink
import com.yugentech.sessions.ui.auth.components.IconCarousel
import com.yugentech.sessions.ui.auth.components.dialog.ForgotPasswordSuccessDialog
import com.yugentech.sessions.ui.auth.components.forms.SignInForm
import com.yugentech.sessions.ui.auth.state.ForgotPasswordState
import com.yugentech.sessions.ui.dash.mainScreen.components.ToastMessage

/** Welcome back, under the icon carousel: laid out as Hajime's sign-in. */
@Composable
fun SignInScreen(
    authViewModel: AuthViewModel,
    onSignIn: (email: String, password: String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onForgotPassword: (email: String) -> Unit,
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val forgotPasswordState by authViewModel.forgotPasswordState.collectAsStateWithLifecycle()

    AuthScaffold(
        title = stringResource(R.string.welcome_back),
        subtitle = stringResource(R.string.sign_in_message),
        icon = {
            IconCarousel(
                modifier = Modifier.padding(vertical = MaterialTheme.spacing.s)
            )
        },
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

                (forgotPasswordState as? ForgotPasswordState.Error)?.let { error ->
                    ToastMessage(
                        message = error.message,
                        onDismiss = { authViewModel.clearForgotPasswordState() },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .statusBarsPadding()
                            .zIndex(1f)
                    )
                }
            }
        }
    ) {
        SignInForm(
            isLoading = authState.isLoading,
            onClearError = { authViewModel.clearError() },
            onSignIn = onSignIn,
            onGoogleSignIn = onGoogleSignIn,
            onForgotPassword = onForgotPassword
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

        AuthSwitchLink(
            prompt = "New to Sessions?",
            action = "Create an account",
            onClick = onNavigateToSignUp
        )
    }

    if (forgotPasswordState is ForgotPasswordState.Success) {
        ForgotPasswordSuccessDialog(
            onDismiss = { authViewModel.clearForgotPasswordState() }
        )
    }
}
