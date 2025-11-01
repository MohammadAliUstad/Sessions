package com.yugentech.sessions.ui.auth.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.yugentech.sessions.ui.auth.utils.FormValidator
import com.yugentech.sessions.ui.auth.utils.SignInFormState
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import kotlinx.coroutines.launch

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

    LaunchedEffect(formState.email, formState.password) {
        onClearError()
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(MaterialTheme.corners.large)
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.l)
        ) {
            Text(
                text = "Welcome back",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.W200
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

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

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

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

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.s))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        if (formState.email.isBlank()) {
                            formState = formState.copy(
                                emailError = "Please enter your email first"
                            )
                        } else {
                            onForgotPassword(formState.email)
                        }
                    }
                ) {
                    Text(
                        text = "Forgot Password?",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.s))

            ActionButton(
                text = "Sign In",
                isLoading = isLoading,
                onClick = {
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

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

            GoogleSignInButton(
                isLoading = isLoading,
                onClick = { scope.launch { onGoogleSignIn() } }
            )
        }
    }
}