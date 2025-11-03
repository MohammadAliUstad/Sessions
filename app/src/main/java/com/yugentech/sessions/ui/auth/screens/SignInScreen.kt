package com.yugentech.sessions.ui.auth.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.auth.components.IconCarousel
import com.yugentech.sessions.ui.auth.components.SignInForm
import com.yugentech.sessions.ui.dash.components.common.ToastMessage
import com.yugentech.sessions.viewModels.LoginViewModel

@Composable
fun SignInScreen(
    loginViewModel: LoginViewModel,
    onSignIn: (email: String, password: String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onForgotPassword: (email: String) -> Unit,
) {
    val state by loginViewModel.authState.collectAsState()
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        ToastMessage(
            message = state.error,
            onDismiss = { loginViewModel.clearError() },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = MaterialTheme.spacing.xl)
                .zIndex(1f)
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = MaterialTheme.spacing.l),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.l))

                IconCarousel(
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.s)
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

                Text(
                    text = "Sessions",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.s))

                Text(
                    text = "Ready to focus and be productive?",
                    style = MaterialTheme.typography.titleMedium,
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

                TextButton(
                    onClick = onNavigateToSignUp,
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.xs)
                ) {
                    Text(
                        text = "Don't have an account? Sign Up",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.l))
            }
        }
    }
}