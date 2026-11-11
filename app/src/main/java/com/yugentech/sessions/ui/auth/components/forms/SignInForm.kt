package com.yugentech.sessions.ui.auth.components.forms

import androidx.compose.ui.platform.LocalContext
import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
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
import com.yugentech.sessions.ui.auth.components.buttons.GuestSignInButton
import com.yugentech.sessions.ui.auth.state.SignInFormState
import com.yugentech.sessions.ui.auth.util.FormValidator
import kotlinx.coroutines.launch

/** Email and password, then Google and Guest side by side underneath. */
@Composable
fun SignInForm(
    isLoading: Boolean,
    onSignIn: (email: String, password: String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onForgotPassword: (email: String) -> Unit,
    onClearError: () -> Unit,
    onContinueAsGuest: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var formState by remember { mutableStateOf(SignInFormState()) }
    // Errors are worked out in callbacks, so read from resources (in the app's language)
    val resources = LocalContext.current.resources
    val scope = rememberCoroutineScope()
    // isLoading is shared by every sign-in path; remember which button started it so only
    // that one shows the spinner while the others are just disabled.
    var loadingFrom by rememberSaveable { mutableStateOf(LOADING_EMAIL) }

    LaunchedEffect(formState.email, formState.password) {
        onClearError()
    }

    // Starting guest mode is instant, so while it does the other buttons keep their look rather
    // than greying out for a split second before the app opens; taps are ignored instead
    val dimForLoading = isLoading && loadingFrom != LOADING_GUEST

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
    ) {
        AppTextField(
            value = formState.email,
            onValueChange = { newEmail ->
                formState = formState.copy(
                    email = newEmail,
                    emailError = FormValidator.validateEmail(resources, newEmail)
                )
            },
            label = stringResource(R.string.label_email),
            leadingIcon = Icons.Default.Email,
            error = formState.emailError
        )

        Column {
            AppTextField(
                value = formState.password,
                onValueChange = { newPassword ->
                    formState = formState.copy(
                        password = newPassword,
                        passwordError = FormValidator.validatePassword(resources, newPassword)
                    )
                },
                label = stringResource(R.string.label_password),
                leadingIcon = Icons.Default.Lock,
                error = formState.passwordError,
                isPassword = true
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(
                    onClick = {
                        if (formState.email.isBlank()) {
                            formState = formState.copy(
                                emailError = resources.getString(R.string.sign_in_reset_needs_email)
                            )
                        } else {
                            onForgotPassword(formState.email)
                        }
                    },
                    modifier = Modifier.height(MaterialTheme.components.buttonSmall)
                ) {
                    Text(
                        text = stringResource(R.string.sign_in_forgot_password),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        ActionButton(
            text = stringResource(R.string.sign_in_action),
            isLoading = isLoading && loadingFrom == LOADING_EMAIL,
            enabled = !dimForLoading,
            onClick = {
                if (isLoading) return@ActionButton
                loadingFrom = LOADING_EMAIL
                val isValid = FormValidator.validateSignInForm(
                    res = resources,
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

        AuthDivider(text = stringResource(R.string.auth_divider_or))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
        ) {
            GoogleSignInButton(
                isLoading = isLoading && loadingFrom == LOADING_GOOGLE,
                enabled = !dimForLoading,
                label = stringResource(R.string.google_short),
                onClick = {
                    if (isLoading) return@GoogleSignInButton
                    loadingFrom = LOADING_GOOGLE
                    scope.launch { onGoogleSignIn() }
                },
                modifier = Modifier.weight(1f)
            )

            // Use the app without an account; the data stays on this phone (Settings can export it)
            GuestSignInButton(
                // Guest mode starts instantly on the phone, so no spinner: the ghost's flight
                // is the wait, and the button stays as it is until the app opens
                isLoading = false,
                enabled = !dimForLoading,
                onClick = {
                    if (isLoading) return@GuestSignInButton
                    loadingFrom = LOADING_GUEST
                    onContinueAsGuest()
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private const val LOADING_EMAIL = "email"
private const val LOADING_GOOGLE = "google"
private const val LOADING_GUEST = "guest"
