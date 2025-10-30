package com.yugentech.sessions.ui.screens

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.yugentech.sessions.ui.Tokens
import com.yugentech.sessions.ui.components.common.ToastMessage
import com.yugentech.sessions.ui.components.signInUp.ForgotPasswordDialog
import com.yugentech.sessions.ui.components.signInUp.IconCarousel
import com.yugentech.sessions.ui.components.signInUp.SignInForm
import com.yugentech.sessions.viewModels.LoginViewModel

@Composable
fun SignInScreen(
    loginViewModel: LoginViewModel,
    onSignInClick: (email: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onNavigateToSignUp: () -> Unit,
) {
    val tokens = Tokens
    val state by loginViewModel.authState.collectAsState()
    val forgotPasswordState by loginViewModel.forgotPasswordState.collectAsState()
    val scrollState = rememberScrollState()
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var currentEmail by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        ToastMessage(
            message = state.error,
            onDismiss = { loginViewModel.clearError() },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = tokens.spacing.xl)
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
                    .padding(horizontal = tokens.spacing.l),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(tokens.spacing.xxl))

                IconCarousel(
                    modifier = Modifier.padding(vertical = tokens.spacing.s)
                )

                Spacer(modifier = Modifier.height(tokens.spacing.m))

                Text(
                    text = "Sessions",
                    // FIX: Used 'tokens.typography.label' since 'title' doesn't exist yet.
                    // I also matched the base style (bodyLarge) to what you used in AppTextField
                    // for consistency, but you can change this back to headlineMedium if you prefer.
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = tokens.typography.label.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(tokens.spacing.s))

                Text(
                    text = "Ready to focus and be productive?",
                    // FIX: Used 'tokens.typography.body' since 'subtitle' doesn't exist.
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = tokens.typography.body.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(tokens.spacing.l))

                SignInForm(
                    isLoading = state.isLoading,
                    onSignInClick = onSignInClick,
                    onGoogleSignInClick = onGoogleSignInClick,
                    onForgotPasswordClick = { email ->
                        currentEmail = email
                        showForgotPasswordDialog = true
                    },
                    onClearError = { loginViewModel.clearError() },
                    onEmailChange = { currentEmail = it }
                )

                TextButton(
                    onClick = onNavigateToSignUp,
                    modifier = Modifier.padding(vertical = tokens.spacing.xs)
                ) {
                    Text(
                        text = "Don't have an account? Sign Up",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = tokens.typography.label.sp
                        ),
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(tokens.spacing.l))
            }

            ForgotPasswordDialog(
                isVisible = showForgotPasswordDialog,
                forgotPasswordState = forgotPasswordState,
                initialEmail = currentEmail,
                onDismiss = {
                    showForgotPasswordDialog = false
                    loginViewModel.clearForgotPasswordState()
                },
                onSendResetEmail = { email ->
                    loginViewModel.forgotPassword(email)
                },
                onClearState = {
                    loginViewModel.clearForgotPasswordState()
                }
            )
        }
    }
}