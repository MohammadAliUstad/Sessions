package com.yugentech.sessions.ui.auth.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import com.yugentech.sessions.ui.auth.utils.FormValidator
import com.yugentech.sessions.ui.auth.utils.SignUpFormState
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
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

    // Clear external errors when user types
    LaunchedEffect(
        formState.name,
        formState.email,
        formState.password,
        formState.confirmPassword
    ) {
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
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.W200
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

            // Name field
            AppTextField(
                value = formState.name,
                onValueChange = { newName ->
                    formState = formState.copy(
                        name = newName,
                        nameError = FormValidator.validateName(newName)
                    )
                },
                label = "Full Name",
                leadingIcon = Icons.Default.Person,
                error = formState.nameError
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

            // Email field
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

            // Password field
            AppTextField(
                value = formState.password,
                onValueChange = { newPassword ->
                    formState = formState.copy(
                        password = newPassword,
                        passwordError = FormValidator.validatePassword(newPassword),
                        // Also revalidate confirm password if it's not empty
                        confirmPasswordError = if (formState.confirmPassword.isNotEmpty()) {
                            FormValidator.validateConfirmPassword(
                                newPassword,
                                formState.confirmPassword
                            )
                        } else {
                            formState.confirmPasswordError
                        }
                    )
                },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                error = formState.passwordError,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

            // Confirm password field
            AppTextField(
                value = formState.confirmPassword,
                onValueChange = { newConfirmPassword ->
                    formState = formState.copy(
                        confirmPassword = newConfirmPassword,
                        confirmPasswordError = FormValidator.validateConfirmPassword(
                            formState.password,
                            newConfirmPassword
                        )
                    )
                },
                label = "Confirm Password",
                leadingIcon = Icons.Default.Lock,
                error = formState.confirmPasswordError,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.l))

            // Create account button
            ActionButton(
                text = "Create Account",
                isLoading = isLoading,
                onClick = {
                    val isValid = FormValidator.validateSignUpForm(
                        name = formState.name,
                        email = formState.email,
                        password = formState.password,
                        confirmPassword = formState.confirmPassword,
                        onNameError = { error ->
                            formState = formState.copy(nameError = error)
                        },
                        onEmailError = { error ->
                            formState = formState.copy(emailError = error)
                        },
                        onPasswordError = { error ->
                            formState = formState.copy(passwordError = error)
                        },
                        onConfirmPasswordError = { error ->
                            formState = formState.copy(confirmPasswordError = error)
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

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

            GoogleSignInButton(
                isLoading = isLoading,
                onClick = { scope.launch { onGoogleSignIn() } }
            )
        }
    }
}