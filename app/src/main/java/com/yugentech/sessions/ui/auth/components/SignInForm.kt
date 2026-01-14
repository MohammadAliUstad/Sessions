package com.yugentech.sessions.ui.auth.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.auth.utils.FormValidator
import com.yugentech.sessions.ui.auth.states.SignInFormState
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
    val context = LocalContext.current

    LaunchedEffect(formState.email, formState.password) {
        onClearError()
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            // Expressive: Use surfaceContainer for a distinct "island"
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        // Expressive: Extra Large corners (28.dp) make it feel friendlier
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.l),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
        ) {
            Text(
                text = stringResource(R.string.welcome_back),
                style = MaterialTheme.typography.headlineSmall.copy( // Bumped up slightly
                    fontWeight = FontWeight.Bold // Bolder header
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Grouping Inputs together
            Column(
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
                    label = stringResource(R.string.label_email),
                    leadingIcon = Icons.Default.Email,
                    error = formState.emailError
                )

                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
                    AppTextField(
                        value = formState.password,
                        onValueChange = { newPassword ->
                            formState = formState.copy(
                                password = newPassword,
                                passwordError = FormValidator.validatePassword(newPassword)
                            )
                        },
                        label = stringResource(R.string.label_password),
                        leadingIcon = Icons.Default.Lock,
                        error = formState.passwordError,
                        isPassword = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                if (formState.email.isBlank()) {
                                    formState = formState.copy(
                                        emailError = context.getString(R.string.email_error)
                                    )
                                } else {
                                    onForgotPassword(formState.email)
                                }
                            },
                            modifier = Modifier
                                .padding(end = MaterialTheme.spacing.xs)
                                .height(MaterialTheme.components.buttonMedium)
                        ) {
                            Text(
                                text = stringResource(R.string.forgot_password),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Actions
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)) {
                ActionButton(
                    text = stringResource(R.string.sign_in),
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

                GoogleSignInButton(
                    isLoading = isLoading,
                    onClick = { scope.launch { onGoogleSignIn() } }
                )
            }
        }
    }
}