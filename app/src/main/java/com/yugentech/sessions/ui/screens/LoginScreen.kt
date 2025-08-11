package com.yugentech.sessions.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.R
import com.yugentech.sessions.viewModels.LoginViewModel
import kotlinx.coroutines.launch

// Material 3 standard spacing
private val spacing = object {
    val xs = 4.dp      // 4dp
    val sm = 8.dp      // 8dp
    val md = 16.dp     // 16dp
    val lg = 24.dp     // 24dp
    val xl = 32.dp     // 32dp
    val xxl = 48.dp    // 48dp
    val xxxl = 64.dp   // 64dp
}

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onSignInClick: (email: String, password: String) -> Unit,
    onSignUpClick: (name: String, email: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit,
) {
    val state by loginViewModel.authState.collectAsState()
    var isLogin by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(spacing.xl))

            AppLogo()

            Spacer(modifier = Modifier.height(spacing.lg))

            AppTitle()

            Spacer(modifier = Modifier.height(spacing.sm))

            AppSubtitle(isLogin = isLogin)

            Spacer(modifier = Modifier.height(spacing.xl))

            ErrorMessage(
                error = state.error,
                onDismiss = { loginViewModel.clearError() }
            )

            LoginForm(
                isLogin = isLogin,
                isLoading = state.isLoading,
                onSignInClick = onSignInClick,
                onSignUpClick = onSignUpClick,
                onGoogleSignInClick = onGoogleSignInClick,
                onClearError = { loginViewModel.clearError() }
            )

            Spacer(modifier = Modifier.height(spacing.md))

            ModeToggle(
                isLogin = isLogin,
                onToggle = {
                    isLogin = !isLogin
                    loginViewModel.clearError()
                }
            )

            Spacer(modifier = Modifier.height(spacing.xl))
        }
    }
}

@Composable
private fun AppLogo() {
    Card(
        modifier = Modifier.size(96.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(spacing.lg)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LogoIcons()
        }
    }
}

@Composable
private fun LogoIcons() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.xs)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LogoIcon(
                icon = Icons.Default.Timer,
                size = 20.dp,
                iconSize = 12.dp,
                color = MaterialTheme.colorScheme.primary
            )
            LogoIcon(
                icon = Icons.Default.Analytics,
                size = 16.dp,
                iconSize = 10.dp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        LogoIcon(
            icon = Icons.Default.Schedule,
            size = 24.dp,
            iconSize = 14.dp,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
private fun LogoIcon(
    icon: ImageVector,
    size: androidx.compose.ui.unit.Dp,
    iconSize: androidx.compose.ui.unit.Dp,
    color: androidx.compose.ui.graphics.Color
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
private fun AppTitle() {
    Text(
        text = "Sessions",
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun AppSubtitle(isLogin: Boolean) {
    AnimatedContent(
        targetState = isLogin,
        transitionSpec = {
            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
        },
        label = "subtitle"
    ) { login ->
        Text(
            text = if (login) "Ready to focus and be productive?"
            else "Start your productivity journey",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = spacing.md)
        )
    }
}

@Composable
private fun ErrorMessage(
    error: String?,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = error != null,
        enter = slideInVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        exit = slideOutVertically() + fadeOut(),
        modifier = Modifier.padding(bottom = if (error != null) spacing.md else 0.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(spacing.md),
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
                    modifier = Modifier.size(spacing.lg)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(spacing.md)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginForm(
    isLogin: Boolean,
    isLoading: Boolean,
    onSignInClick: (email: String, password: String) -> Unit,
    onSignUpClick: (name: String, email: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onClearError: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // Clear errors when switching modes
    LaunchedEffect(isLogin) {
        nameError = ""
        emailError = ""
        passwordError = ""
        onClearError()
    }

    // Clear backend errors when user types
    LaunchedEffect(name, email, password) {
        onClearError()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(spacing.lg)
    ) {
        Column(modifier = Modifier.padding(spacing.lg)) {
            AnimatedContent(
                targetState = isLogin,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                },
                label = "form_title"
            ) { login ->
                Text(
                    text = if (login) "Sign In" else "Create Account",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(spacing.lg))

            // Name field for sign up
            AnimatedVisibility(
                visible = !isLogin,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ) + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    InputField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = validateName(it)
                        },
                        label = "Full Name",
                        icon = Icons.Default.Person,
                        error = nameError
                    )
                    Spacer(modifier = Modifier.height(spacing.md))
                }
            }

            // Email field
            InputField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = validateEmail(it)
                },
                label = "Email",
                icon = Icons.Default.Email,
                error = emailError
            )

            Spacer(modifier = Modifier.height(spacing.md))

            // Password field
            PasswordField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = validatePassword(it)
                },
                passwordVisible = passwordVisible,
                onVisibilityChange = { passwordVisible = it },
                error = passwordError
            )

            Spacer(modifier = Modifier.height(spacing.xl))

            // Sign in/up button
            ActionButton(
                text = if (isLogin) "Sign In" else "Create Account",
                isLoading = isLoading,
                onClick = {
                    if (validateForm(
                            name = name,
                            email = email,
                            password = password,
                            isLogin = isLogin,
                            onNameError = { nameError = it },
                            onEmailError = { emailError = it },
                            onPasswordError = { passwordError = it }
                        )
                    ) {
                        scope.launch {
                            if (isLogin) {
                                onSignInClick(email, password)
                            } else {
                                onSignUpClick(name, email, password)
                            }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(spacing.md))

            // Google sign in button
            GoogleSignInButton(
                isLoading = isLoading,
                onClick = { scope.launch { onGoogleSignInClick() } }
            )
        }
    }
}

@Composable
private fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    error: String = ""
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = error.isNotEmpty(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (error.isNotEmpty()) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            }
        )
        AnimatedVisibility(
            visible = error.isNotEmpty(),
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = spacing.sm, top = spacing.xs)
            )
        }
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    passwordVisible: Boolean,
    onVisibilityChange: (Boolean) -> Unit,
    error: String = ""
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Password") },
            isError = error.isNotEmpty(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (error.isNotEmpty()) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                val alpha by animateFloatAsState(
                    targetValue = if (value.isNotEmpty()) 1f else 0.6f,
                    label = "password_visibility_alpha"
                )
                IconButton(
                    onClick = { onVisibilityChange(!passwordVisible) },
                    modifier = Modifier.alpha(alpha)
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Password Visibility",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation()
        )
        AnimatedVisibility(
            visible = error.isNotEmpty(),
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = spacing.sm, top = spacing.xs)
            )
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = {
                fadeIn(tween(200)) togetherWith fadeOut(tween(200))
            },
            label = "button_content"
        ) { loading ->
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun GoogleSignInButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        enabled = !isLoading
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google_icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Continue with Google",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun ModeToggle(
    isLogin: Boolean,
    onToggle: () -> Unit
) {
    TextButton(onClick = onToggle) {
        AnimatedContent(
            targetState = isLogin,
            transitionSpec = {
                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
            },
            label = "toggle_text"
        ) { login ->
            Text(
                text = if (login) "Don't have an account? Sign Up"
                else "Already have an account? Sign In",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
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

private fun validateEmail(email: String): String {
    return when {
        email.isBlank() -> "Email cannot be empty"
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
            "Please enter a valid email"

        else -> ""
    }
}

private fun validatePassword(password: String): String {
    return when {
        password.isBlank() -> "Password cannot be empty"
        password.length < 6 -> "Password must be at least 6 characters"
        else -> ""
    }
}

private fun validateForm(
    name: String,
    email: String,
    password: String,
    isLogin: Boolean,
    onNameError: (String) -> Unit,
    onEmailError: (String) -> Unit,
    onPasswordError: (String) -> Unit
): Boolean {
    var isValid = true

    if (!isLogin) {
        val nameError = validateName(name)
        onNameError(nameError)
        if (nameError.isNotEmpty()) isValid = false
    }

    val emailError = validateEmail(email)
    onEmailError(emailError)
    if (emailError.isNotEmpty()) isValid = false

    val passwordError = validatePassword(password)
    onPasswordError(passwordError)
    if (passwordError.isNotEmpty()) isValid = false

    return isValid
}