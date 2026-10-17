package com.yugentech.sessions.ui.auth.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.auth.components.AppTextField
import com.yugentech.sessions.ui.auth.components.AuthDivider
import com.yugentech.sessions.ui.auth.components.buttons.ActionButton
import com.yugentech.sessions.ui.auth.components.buttons.GoogleSignInButton
import com.yugentech.sessions.ui.auth.state.SignUpFormState
import com.yugentech.sessions.ui.auth.util.FormValidator
import kotlinx.coroutines.launch

/** A name, email and password, then Google underneath, as on Hajime's sign-up. */
@Composable
fun SignUpForm(
    isLoading: Boolean,
    onSignUp: (name: String, email: String, password: String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    var formState by remember { mutableStateOf(SignUpFormState()) }
    val scope = rememberCoroutineScope()
    // isLoading is shared by both sign-up paths; remember which button started it so only
    // that one shows the spinner while the other is just disabled.
    var isGoogleSignIn by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(
        formState.name,
        formState.email,
        formState.password
    ) {
        onClearError()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
    ) {
        AppTextField(
            value = formState.name,
            onValueChange = { newName ->
                formState = formState.copy(
                    name = newName,
                    nameError = FormValidator.validateName(newName)
                )
            },
            label = "Your name",
            leadingIcon = Icons.Default.Person,
            error = formState.nameError
        )

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

        ActionButton(
            text = "Create account",
            isLoading = isLoading && !isGoogleSignIn,
            enabled = !isLoading,
            onClick = {
                isGoogleSignIn = false
                val isValid = FormValidator.validateSignUpForm(
                    name = formState.name,
                    email = formState.email,
                    password = formState.password,
                    onNameError = { error ->
                        formState = formState.copy(nameError = error)
                    },
                    onEmailError = { error ->
                        formState = formState.copy(emailError = error)
                    },
                    onPasswordError = { error ->
                        formState = formState.copy(passwordError = error)
                    }
                )

                if (isValid) {
                    scope.launch {
                        onSignUp(
                            formState.name,
                            formState.email,
                            formState.password
                        )
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
