package com.yugentech.sessions.ui.components.signInUp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import com.yugentech.sessions.ui.Tokens

@Composable
fun SignInForm(
    isLoading: Boolean,
    onSignInClick: (email: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onForgotPasswordClick: (email: String) -> Unit,
    onClearError: () -> Unit,
    onEmailChange: (String) -> Unit
) {
    val tokens = Tokens
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
        // This is a correct use of your tokens
        shape = RoundedCornerShape(tokens.corners.large),
        // This is also a correct use of your tokens
        elevation = CardDefaults.cardElevation(tokens.elevation.level1)
    ) {
        Column(
            // Correct token usage for padding
            modifier = Modifier.padding(tokens.spacing.l)
        ) {
            Text(
                text = "Welcome back",
                // MODIFIED: Removed 'fontSize = tokens.typography.title.sp'
                // 'tokens.typography' is not defined in your provided Tokens.kt file.
                // This now uses the MaterialTheme's headlineMedium style and applies your desired font weight.
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.W200
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Correct token usage
            Spacer(modifier = Modifier.height(tokens.spacing.m))

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

            // Correct token usage
            Spacer(modifier = Modifier.height(tokens.spacing.m))

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

            // Correct token usage
            Spacer(modifier = Modifier.height(tokens.spacing.s))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onForgotPasswordClick(email) }) {
                    Text(
                        text = "Forgot Password?",
                        // MODIFIED: Removed 'fontSize = tokens.typography.label.sp'
                        // This now correctly uses the MaterialTheme's labelLarge style.
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Correct token usage
            Spacer(modifier = Modifier.height(tokens.spacing.s))

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

            // Correct token usage
            Spacer(modifier = Modifier.height(tokens.spacing.m))

            GoogleSignInButton(
                isLoading = isLoading,
                onClick = { scope.launch { onGoogleSignInClick() } }
            )
        }
    }
}