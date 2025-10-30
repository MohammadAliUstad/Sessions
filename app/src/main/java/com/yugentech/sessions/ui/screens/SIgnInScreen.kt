package com.yugentech.sessions.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.yugentech.sessions.ui.components.common.ToastMessage
import com.yugentech.sessions.ui.components.signInUp.*
import com.yugentech.sessions.viewModels.LoginViewModel

@Composable
fun SignInScreen(
    loginViewModel: LoginViewModel,
    onSignInClick: (email: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onNavigateToSignUp: () -> Unit,
) {
    val tokens = AppTokens.current()
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
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = tokens.typography.title.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(tokens.spacing.s))

                Text(
                    text = "Ready to focus and be productive?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = tokens.typography.subtitle.sp
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