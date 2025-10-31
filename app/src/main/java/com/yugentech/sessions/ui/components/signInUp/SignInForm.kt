package com.yugentech.sessions.ui.components.signInUp

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
import com.yugentech.sessions.ui.corners
import com.yugentech.sessions.ui.screens.validateEmail
import com.yugentech.sessions.ui.screens.validatePassword
import com.yugentech.sessions.ui.spacing
import com.yugentech.sessions.utils.validateSignInForm
import kotlinx.coroutines.launch

@Composable
fun SignInForm(
    isLoading: Boolean,
    onSignInClick: (email: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onForgotPasswordClick: (email: String) -> Unit,
    onClearError: () -> Unit,
    onEmailChange: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(email, password) { onClearError() }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                value = email,
                onValueChange = {
                    email = it
                    onEmailChange(it)
                    emailError = validateEmail(it)
                },
                label = "Email",
                leadingIcon = Icons.Default.Email,
                error = emailError
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

            AppTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = validatePassword(it)
                },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                error = passwordError,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.s))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onForgotPasswordClick(email) }) {
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
                    if (validateSignInForm(
                            email = email,
                            password = password,
                            onEmailError = { emailError = it },
                            onPasswordError = { passwordError = it }
                        )
                    ) {
                        scope.launch { onSignInClick(email, password) }
                    }
                }
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

            GoogleSignInButton(
                isLoading = isLoading,
                onClick = { scope.launch { onGoogleSignInClick() } }
            )
        }
    }
}