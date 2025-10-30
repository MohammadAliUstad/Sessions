package com.yugentech.sessions.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.yugentech.sessions.ui.components.common.ToastMessage
import com.yugentech.sessions.ui.components.signInUp.ActionButton
import com.yugentech.sessions.ui.components.signInUp.AppTextField
import com.yugentech.sessions.ui.components.signInUp.GoogleSignInButton
import com.yugentech.sessions.viewModels.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    loginViewModel: LoginViewModel,
    onSignUpClick: (name: String, email: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    val state by loginViewModel.authState.collectAsState()
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Toast positioned at top
        ToastMessage(
            message = state.error,
            onDismiss = { loginViewModel.clearError() },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp)
                .zIndex(1f)
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Sessions",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Start your productivity journey",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                SignUpForm(
                    isLoading = state.isLoading,
                    onSignUpClick = onSignUpClick,
                    onGoogleSignInClick = onGoogleSignInClick,
                    onClearError = { loginViewModel.clearError() }
                )

                TextButton(
                    onClick = onNavigateToSignIn,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "Already have an account? Sign In",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SignUpForm(
    isLoading: Boolean,
    onSignUpClick: (name: String, email: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onClearError: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(name, email, password, confirmPassword) {
        onClearError()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.W200,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = validateName(it)
                },
                label = "Full Name",
                leadingIcon = Icons.Default.Person,
                error = nameError
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = validateEmail(it)
                },
                label = "Email",
                leadingIcon = Icons.Default.Email,
                error = emailError
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = validatePassword(it)
                    if (confirmPassword.isNotEmpty()) {
                        confirmPasswordError = validateConfirmPassword(it, confirmPassword)
                    }
                },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                error = passwordError,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = validateConfirmPassword(password, it)
                },
                label = "Confirm Password",
                leadingIcon = Icons.Default.Lock,
                error = confirmPasswordError,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            ActionButton(
                text = "Create Account",
                isLoading = isLoading,
                onClick = {
                    if (validateSignUpForm(
                            name = name,
                            email = email,
                            password = password,
                            confirmPassword = confirmPassword,
                            onNameError = { nameError = it },
                            onEmailError = { emailError = it },
                            onPasswordError = { passwordError = it },
                            onConfirmPasswordError = { confirmPasswordError = it }
                        )
                    ) {
                        scope.launch {
                            onSignUpClick(name, email, password)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            GoogleSignInButton(
                isLoading = isLoading,
                onClick = { scope.launch { onGoogleSignInClick() } }
            )
        }
    }
}

// Validation functions
private fun validateName(name: String): String {
    return when {
        name.isBlank() -> "Name cannot be empty"
        name.length < 2 -> "Name must be at least 2 characters"
        else -> ""
    }
}

fun validateEmail(email: String): String {
    return when {
        email.isBlank() -> "Email cannot be empty"
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
            "Please enter a valid email"
        else -> ""
    }
}

fun validatePassword(password: String): String {
    return when {
        password.isBlank() -> "Password cannot be empty"
        password.length < 8 -> "Password must be at least 8 characters"
        !password.any { it.isDigit() } -> "Password must contain at least one number"
        !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
        !password.any { it.isLowerCase() } -> "Password must contain at least one lowercase letter"
        else -> ""
    }
}

private fun validateConfirmPassword(password: String, confirmPassword: String): String {
    return when {
        confirmPassword.isBlank() -> "Please confirm your password"
        password != confirmPassword -> "Passwords do not match"
        else -> ""
    }
}

private fun validateSignUpForm(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    onNameError: (String) -> Unit,
    onEmailError: (String) -> Unit,
    onPasswordError: (String) -> Unit,
    onConfirmPasswordError: (String) -> Unit
): Boolean {
    var isValid = true

    val nameError = validateName(name)
    onNameError(nameError)
    if (nameError.isNotEmpty()) isValid = false

    val emailError = validateEmail(email)
    onEmailError(emailError)
    if (emailError.isNotEmpty()) isValid = false

    val passwordError = validatePassword(password)
    onPasswordError(passwordError)
    if (passwordError.isNotEmpty()) isValid = false

    val confirmPasswordError = validateConfirmPassword(password, confirmPassword)
    onConfirmPasswordError(confirmPasswordError)
    if (confirmPasswordError.isNotEmpty()) isValid = false

    return isValid
}