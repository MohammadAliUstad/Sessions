package com.yugentech.sessions.ui.auth.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.auth.components.AppTextField
import com.yugentech.sessions.ui.auth.components.AuthDivider
import com.yugentech.sessions.ui.auth.components.buttons.ActionButton
import com.yugentech.sessions.ui.auth.components.buttons.GoogleSignInButton
import com.yugentech.sessions.ui.auth.state.SignInFormState
import com.yugentech.sessions.ui.auth.util.FormValidator
import kotlinx.coroutines.launch

/** Email and password, then Google underneath, as on Hajime's sign-in. */
@Composable
fun SignInForm(
    isLoading: Boolean,
    onSignIn: (email: String, password: String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onForgotPassword: (email: String) -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    var formState by remember { mutableStateOf(SignInFormState()) }
    val scope = rememberCoroutineScope()
    // isLoading is shared by both sign-in paths; remember which button started it so only
    // that one shows the spinner while the other is just disabled.
    var isGoogleSignIn by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(formState.email, formState.password) {
        onClearError()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
    ) {
        AppTextField(
            value = formState.email,
            onValueChange = { newEmail ->
                formState = formState.copy(
                    email = newEmail,
                    emailError = FormValidator.validateEmail(newEmail)
                )
            },
            label = "Email",
            leadingIcon = Icons.Default.Email,
            error = formState.emailError
        )

        Column {
            AppTextField(
                value = formState.password,
                onValueChange = { newPassword ->
                    formState = formState.copy(
                        password = newPassword,
                        passwordError = FormValidator.validatePassword(newPassword)
                    )
                },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                error = formState.passwordError,
                isPassword = true
            )

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(
                    onClick = {
                        if (formState.email.isBlank()) {
                            formState = formState.copy(
                                emailError = "Enter your email first, and we'll send a reset link"
                            )
                        } else {
                            onForgotPassword(formState.email)
                        }
                    },
                    modifier = Modifier.height(MaterialTheme.components.buttonSmall)
                ) {
                    Text(
                        text = "Forgot password?",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        ActionButton(
            text = "Sign in",
            isLoading = isLoading && !isGoogleSignIn,
            enabled = !isLoading,
            onClick = {
                isGoogleSignIn = false
                val isValid = FormValidator.validateSignInForm(
                    email = formState.email,
                    password = formState.password,
                    onEmailError = { error ->
                        formState = formState.copy(emailError = error)
                    },
                    onPasswordError = { error ->
                        formState = formState.copy(passwordError = error)
                    }
                )

                if (isValid) {
                    scope.launch {
                        onSignIn(formState.email, formState.password)
                    }
                }
            }
        )

        AuthDivider(text = "or")

        GoogleSignInButton(
            isLoading = isLoading && isGoogleSignIn,
            enabled = !isLoading,
            onClick = {
                isGoogleSignIn = true
                scope.launch { onGoogleSignIn() }
            }
        )
    }
}
