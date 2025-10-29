package com.yugentech.sessions.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.yugentech.sessions.ui.AppTokens
import com.yugentech.sessions.ui.components.common.ToastMessage
import com.yugentech.sessions.ui.components.signInUp.*
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
    val tokens = AppTokens.current()
    val state by loginViewModel.authState.collectAsState()
    val forgotPasswordState by loginViewModel.forgotPasswordState.collectAsState()
    val scrollState = rememberScrollState()
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var currentEmail by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        ToastMessage(
            message = state.error,
            onDismiss = { loginViewModel.clearError() },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = tokens.spacing.xl)
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
                    .padding(horizontal = tokens.spacing.l),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(tokens.spacing.xxl))

                IconCarousel(
                    modifier = Modifier.padding(vertical = tokens.spacing.s)
                )

                Spacer(modifier = Modifier.height(tokens.spacing.m))

                Text(
                    text = "Sessions",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = tokens.typography.title.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(tokens.spacing.s))

                Text(
                    text = "Ready to focus and be productive?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = tokens.typography.subtitle.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(tokens.spacing.l))

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
                    modifier = Modifier.padding(vertical = tokens.spacing.xs)
                ) {
                    Text(
                        text = "Don't have an account? Sign Up",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = tokens.typography.label.sp
                        ),
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(tokens.spacing.l))
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
}

@Composable
private fun IconCarousel(
    modifier: Modifier = Modifier
) {
    val tokens = AppTokens.current()
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

    val circleSize = tokens.components.imageSizeLarge
    val iconSize = tokens.components.iconMedium

    Box(
        modifier = modifier
            .size(circleSize)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        icons.forEachIndexed { index, icon ->
            val iconAngle = angle + (360f / icons.size * index)
            val radius = with(LocalDensity.current) { (circleSize.toPx() / 2f) }

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
                    .size(iconSize)
                    .offset(x, y),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Icon(
            imageVector = Icons.Filled.Timer,
            contentDescription = null,
            modifier = Modifier.size(tokens.components.iconLarge),
            tint = MaterialTheme.colorScheme.primary
        )
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
    val tokens = AppTokens.current()
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
        shape = RoundedCornerShape(tokens.corners.large),
        elevation = CardDefaults.cardElevation(tokens.elevation.level1)
    ) {
        Column(
            modifier = Modifier.padding(tokens.spacing.l)
        ) {
            Text(
                text = "Welcome back",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = tokens.typography.title.sp,
                    fontWeight = FontWeight.W200
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

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

            Spacer(modifier = Modifier.height(tokens.spacing.s))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onForgotPasswordClick(email) }) {
                    Text(
                        text = "Forgot Password?",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = tokens.typography.label.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

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

            Spacer(modifier = Modifier.height(tokens.spacing.m))

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