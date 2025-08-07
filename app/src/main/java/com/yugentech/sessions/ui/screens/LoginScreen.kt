package com.yugentech.sessions.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.LoginViewModel
import com.yugentech.sessions.R
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onSignInClick: (email: String, password: String) -> Unit,
    onSignUpClick: (name: String, email: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit,
) {
    val state by loginViewModel.authState.collectAsState()
    var isLogin by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Reset errors when switching between login/signup
    LaunchedEffect(isLogin) {
        nameError = ""
        emailError = ""
        passwordError = ""
        loginViewModel.clearError()
    }

    // Clear global error when user starts typing
    LaunchedEffect(name, email, password) {
        if (state.error != null) {
            loginViewModel.clearError()
        }
    }

    // Validation Functions
    fun validateName(name: String): String {
        return when {
            name.isBlank() -> "Name cannot be empty"
            name.length < 2 -> "Name must be at least 2 characters"
            else -> ""
        }
    }

    fun validateEmail(email: String): String {
        return when {
            email.isBlank() -> "Email cannot be empty"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches() -> "Please enter a valid email"
            else -> ""
        }
    }

    fun validatePassword(password: String): String {
        return when {
            password.isBlank() -> "Password cannot be empty"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> ""
        }
    }

    fun validateForm(): Boolean {
        var isValid = true

        if (!isLogin) {
            nameError = validateName(name)
            if (nameError.isNotEmpty()) isValid = false
        }

        emailError = validateEmail(email)
        if (emailError.isNotEmpty()) isValid = false

        passwordError = validatePassword(password)
        if (passwordError.isNotEmpty()) isValid = false

        return isValid
    }

    fun handleAuthAction() {
        if (validateForm()) {
            scope.launch {
                if (isLogin) {
                    onSignInClick(email, password)
                } else {
                    onSignUpClick(name, email, password)
                }
            }
        }
    }

    fun toggleAuthMode() {
        isLogin = !isLogin
        name = ""
        email = ""
        password = ""
        nameError = ""
        emailError = ""
        passwordError = ""
        loginViewModel.clearError()
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // App Branding Section
            AppBrandingSection()

            Spacer(modifier = Modifier.height(48.dp))

            // Error Display
            ErrorDisplay(
                error = state.error,
                onDismiss = { loginViewModel.clearError() }
            )

            // Main Auth Card
            AuthCard(
                isLogin = isLogin,
                name = name,
                email = email,
                password = password,
                passwordVisible = passwordVisible,
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
                isLoading = state.isLoading,
                onNameChange = {
                    name = it
                    nameError = validateName(it)
                },
                onEmailChange = {
                    email = it
                    emailError = validateEmail(it)
                },
                onPasswordChange = {
                    password = it
                    passwordError = validatePassword(it)
                },
                onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                onAuthAction = ::handleAuthAction,
                onGoogleSignIn = { scope.launch { onGoogleSignInClick() } }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Auth Mode Toggle
            AuthModeToggle(
                isLogin = isLogin,
                onToggle = ::toggleAuthMode
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AppBrandingSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Simple Icon Illustration
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(36.dp)
            )
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(32.dp)
            )
        }

        // App Title
        Text(
            text = "Sessions",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Focus • Track • Achieve",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorDisplay(
    error: String?,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = error != null,
        enter = slideInVertically(animationSpec = tween(300)) + fadeIn(),
        exit = slideOutVertically(animationSpec = tween(300)) + fadeOut()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthCard(
    isLogin: Boolean,
    name: String,
    email: String,
    password: String,
    passwordVisible: Boolean,
    nameError: String,
    emailError: String,
    passwordError: String,
    isLoading: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onAuthAction: () -> Unit,
    onGoogleSignIn: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Card Header
            AuthCardHeader(isLogin = isLogin)

            // Form Fields
            AuthFormFields(
                isLogin = isLogin,
                name = name,
                email = email,
                password = password,
                passwordVisible = passwordVisible,
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
                onNameChange = onNameChange,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                onPasswordVisibilityToggle = onPasswordVisibilityToggle
            )

            // Action Buttons
            AuthActionButtons(
                isLogin = isLogin,
                isLoading = isLoading,
                onAuthAction = onAuthAction,
                onGoogleSignIn = onGoogleSignIn
            )
        }
    }
}

@Composable
private fun AuthCardHeader(isLogin: Boolean) {
    AnimatedContent(
        targetState = isLogin,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        },
        label = "AuthHeader"
    ) { loginMode ->
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = if (loginMode) "Welcome back" else "Create account",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (loginMode)
                    "Sign in to continue your journey"
                else
                    "Join us and start your focus journey",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AuthFormFields(
    isLogin: Boolean,
    name: String,
    email: String,
    password: String,
    passwordVisible: Boolean,
    nameError: String,
    emailError: String,
    passwordError: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Name Field (Sign Up Only)
        AnimatedVisibility(
            visible = !isLogin,
            enter = slideInVertically(animationSpec = tween(300)) + fadeIn(),
            exit = slideOutVertically(animationSpec = tween(300)) + fadeOut()
        ) {
            CustomTextField(
                value = name,
                onValueChange = onNameChange,
                label = "Full name",
                leadingIcon = Icons.Default.Person,
                error = nameError
            )
        }

        // Email Field
        CustomTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email address",
            leadingIcon = Icons.Default.Email,
            error = emailError
        )

        // Password Field
        CustomTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Password",
            leadingIcon = Icons.Default.Lock,
            error = passwordError,
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onPasswordVisibilityToggle) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Default.Visibility
                        else
                            Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }
}

@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    error: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (error.isEmpty())
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.error
                )
            },
            trailingIcon = trailingIcon,
            isError = error.isNotEmpty(),
            singleLine = true,
            visualTransformation = visualTransformation,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                errorContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        AnimatedVisibility(
            visible = error.isNotEmpty(),
            enter = slideInVertically(animationSpec = tween(300)) + fadeIn(),
            exit = slideOutVertically(animationSpec = tween(300)) + fadeOut()
        ) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun AuthActionButtons(
    isLogin: Boolean,
    isLoading: Boolean,
    onAuthAction: () -> Unit,
    onGoogleSignIn: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Primary Action Button
        Button(
            onClick = onAuthAction,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !isLoading
        ) {
            AnimatedContent(
                targetState = isLoading,
                label = "LoadingState"
            ) { loading ->
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (isLogin) "Sign in" else "Create account",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        // Google Sign In Button
        OutlinedButton(
            onClick = onGoogleSignIn,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !isLoading
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google_icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Continue with Google",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun AuthModeToggle(
    isLogin: Boolean,
    onToggle: () -> Unit
) {
    AnimatedContent(
        targetState = isLogin,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        },
        label = "AuthToggle"
    ) { loginMode ->
        TextButton(onClick = onToggle) {
            Text(
                text = if (loginMode)
                    "New here? Create an account"
                else
                    "Already have an account? Sign in",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}