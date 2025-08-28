package com.yugentech.sessions.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.ui.components.ActionButton
import com.yugentech.sessions.ui.components.AppTextField
import com.yugentech.sessions.ui.components.ForgotPasswordDialog
import com.yugentech.sessions.ui.components.GoogleSignInButton
import com.yugentech.sessions.viewModels.LoginViewModel
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SignInScreen(
    loginViewModel: LoginViewModel,
    onSignInClick: (email: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onNavigateToSignUp: () -> Unit,
) {
    val state by loginViewModel.authState.collectAsState()
    val forgotPasswordState by loginViewModel.forgotPasswordState.collectAsState()
    val scrollState = rememberScrollState()
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var currentEmail by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            IconCarousel(
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Sessions",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ready to focus and be productive?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            ErrorMessage(
                error = state.error,
                onDismiss = { loginViewModel.clearError() }
            )

            SignInForm(
                isLoading = state.isLoading,
                onSignInClick = onSignInClick,
                onGoogleSignInClick = onGoogleSignInClick,
                onForgotPasswordClick = { email ->
                    currentEmail = email
                    showForgotPasswordDialog = true
                },
                onClearError = { loginViewModel.clearError() },
                onEmailChange = { currentEmail = it }
            )

            TextButton(
                onClick = onNavigateToSignUp,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Don't have an account? Sign Up",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        ForgotPasswordDialog(
            isVisible = showForgotPasswordDialog,
            forgotPasswordState = forgotPasswordState,
            initialEmail = currentEmail,
            onDismiss = {
                showForgotPasswordDialog = false
                loginViewModel.clearForgotPasswordState()
            },
            onSendResetEmail = { email ->
                loginViewModel.forgotPassword(email)
            },
            onClearState = {
                loginViewModel.clearForgotPasswordState()
            }
        )
    }
}

@Composable
private fun IconCarousel(
    modifier: Modifier = Modifier
) {
    val icons = listOf(
        Icons.Filled.School,
        Icons.Filled.Work,
        Icons.Filled.Lightbulb,
        Icons.AutoMirrored.Filled.Assignment
    )

    val infiniteTransition = rememberInfiniteTransition(label = "carousel")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 20000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .size(200.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        icons.forEachIndexed { index, icon ->
            val iconAngle = angle + (360f / icons.size * index)
            val radius = 200f

            val x = with(LocalDensity.current) {
                (radius * cos(Math.toRadians(iconAngle.toDouble()))).toFloat().toDp()
            }
            val y = with(LocalDensity.current) {
                (radius * sin(Math.toRadians(iconAngle.toDouble()))).toFloat().toDp()
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .offset(x, y),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Icon(
            imageVector = Icons.Filled.Timer,
            contentDescription = null,
            modifier = Modifier.size(36.dp),
            tint = MaterialTheme.colorScheme.primary
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
        modifier = Modifier.padding(bottom = if (error != null) 16.dp else 0.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SignInForm(
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

    LaunchedEffect(email, password) {
        onClearError()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Welcome back",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.W200,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // In SignInForm
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

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { onForgotPasswordClick(email) }
                ) {
                    Text(
                        text = "Forgot Password?",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                        scope.launch {
                            onSignInClick(email, password)
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

private fun validateSignInForm(
    email: String,
    password: String,
    onEmailError: (String) -> Unit,
    onPasswordError: (String) -> Unit
): Boolean {
    var isValid = true

    val emailError = validateEmail(email)
    onEmailError(emailError)
    if (emailError.isNotEmpty()) isValid = false

    val passwordError = validatePassword(password)
    onPasswordError(passwordError)
    if (passwordError.isNotEmpty()) isValid = false

    return isValid
}