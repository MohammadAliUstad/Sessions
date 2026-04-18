package com.yugentech.sessions.ui.auth.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.auth.components.AppTextField
import com.yugentech.sessions.ui.auth.components.buttons.ActionButton
import com.yugentech.sessions.ui.auth.components.buttons.GoogleSignInButton
import com.yugentech.sessions.ui.auth.state.SignUpFormState
import com.yugentech.sessions.ui.auth.util.FormValidator
import kotlinx.coroutines.launch

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

    LaunchedEffect(
        formState.name,
        formState.email,
        formState.password
    ) {
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
                text = stringResource(R.string.create_account),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)) {
                AppTextField(
                    value = formState.name,
                    onValueChange = { newName ->
                        formState = formState.copy(
                            name = newName,
                            nameError = FormValidator.validateName(newName)
                        )
                    },
                    label = stringResource(R.string.full_name),
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
                    label = stringResource(R.string.label_email),
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
                    label = stringResource(R.string.password),
                    leadingIcon = Icons.Default.Lock,
                    error = formState.passwordError,
                    isPassword = true
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)) {
                ActionButton(
                    text = stringResource(R.string.create_account),
                    isLoading = isLoading,
                    onClick = {
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

                GoogleSignInButton(
                    isLoading = isLoading,
                    onClick = { scope.launch { onGoogleSignIn() } }
                )
            }
        }
    }
}