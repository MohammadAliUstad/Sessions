package com.yugentech.sessions.ui.auth.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.dimensions.AppConstants.ONEF
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.auth.components.IconCarousel
import com.yugentech.sessions.ui.auth.components.SignInForm
import com.yugentech.sessions.ui.dash.components.common.ToastMessage
import com.yugentech.sessions.utils.ForgotPasswordState
import com.yugentech.sessions.viewModels.LoginViewModel

@Composable
fun SignInScreen(
    loginViewModel: LoginViewModel,
    onSignIn: (email: String, password: String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onForgotPassword: (email: String) -> Unit,
) {
    val state by loginViewModel.authState.collectAsStateWithLifecycle()
    val forgotPasswordState by loginViewModel.forgotPasswordState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // Expressive: Use SurfaceContainerLow for depth
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = MaterialTheme.spacing.m),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xl))

                IconCarousel(
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.s)
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

                Text(
                    text = stringResource(R.string.app_name),
                    // Expressive: Bolder, Larger Header
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.s))

                Text(
                    text = stringResource(R.string.sign_in_message),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.l))

                SignInForm(
                    isLoading = state.isLoading,
                    onClearError = { loginViewModel.clearError() },
                    onSignIn = onSignIn,
                    onGoogleSignIn = onGoogleSignIn,
                    onForgotPassword = onForgotPassword
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xsSmall))

                TextButton(
                    onClick = onNavigateToSignUp,
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.xs)
                ) {
                    Text(
                        text = stringResource(R.string.don_t_have_an_account),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.l))
            }

            ToastMessage(
                message = state.error,
                onDismiss = { loginViewModel.clearError() },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = MaterialTheme.spacing.m)
                    .zIndex(ONEF)
            )

            if (forgotPasswordState is ForgotPasswordState.Error) {
                val errorMsg = (forgotPasswordState as ForgotPasswordState.Error).message
                ToastMessage(
                    message = errorMsg,
                    onDismiss = { loginViewModel.clearForgotPasswordState() },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = MaterialTheme.spacing.m)
                        .zIndex(ONEF)
                )
            }
        }

        if (forgotPasswordState is ForgotPasswordState.Success) {
            AlertDialog(
                onDismissRequest = { loginViewModel.clearForgotPasswordState() },
                title = { Text("Check your email") },
                text = {
                    Text("We have sent a password reset link to your email address. Please check your inbox (and spam folder) to reset your password.")
                },
                confirmButton = {
                    TextButton(
                        onClick = { loginViewModel.clearForgotPasswordState() }
                    ) {
                        Text("OK")
                    }
                },
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}