package com.yugentech.sessions.ui.auth.components.forms

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
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.auth.components.AppTextField
import com.yugentech.sessions.ui.auth.components.buttons.ActionButton
import com.yugentech.sessions.ui.auth.components.buttons.GoogleSignInButton
import com.yugentech.sessions.ui.auth.state.SignInFormState
import com.yugentech.sessions.ui.auth.util.FormValidator
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
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(MaterialTheme.corners.extraLarge)
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.l),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
        ) {
            Text(
                text = stringResource(R.string.welcome_back),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)) {
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
