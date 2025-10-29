package com.yugentech.sessions.ui.components.signInUp

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.yugentech.sessions.ui.AppTokens
import com.yugentech.sessions.viewModels.ForgotPasswordState

@Composable
fun ForgotPasswordDialog(
    isVisible: Boolean,
    forgotPasswordState: ForgotPasswordState,
    initialEmail: String = "",
    onDismiss: () -> Unit,
    onSendResetEmail: (String) -> Unit,
    onClearState: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = {
                onClearState()
                onDismiss()
            },
            confirmButton = {},
            dismissButton = {},
            text = {
                ForgotPasswordContent(
                    forgotPasswordState = forgotPasswordState,
                    initialEmail = initialEmail,
                    onSendResetEmail = onSendResetEmail,
                    onDismiss = {
                        onClearState()
                        onDismiss()
                    }
                )
            }
        )
    }
}

@Composable
private fun ForgotPasswordContent(
    forgotPasswordState: ForgotPasswordState,
    initialEmail: String,
    onSendResetEmail: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val tokens = AppTokens.current()
    var email by remember { mutableStateOf(initialEmail) }
    var emailError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = tokens.spacing.m),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (forgotPasswordState) {
            is ForgotPasswordState.Idle -> {
                ForgotPasswordForm(
                    email = email,
                    onEmailChange = {
                        email = it
                        emailError = validateEmail(it)
                    },
                    emailError = emailError,
                    onSendResetEmail = {
                        val error = validateEmail(email)
                        emailError = error
                        if (error.isEmpty()) onSendResetEmail(email)
                    },
                    onCancel = onDismiss
                )
            }

            is ForgotPasswordState.Loading -> ForgotPasswordLoading()
            is ForgotPasswordState.Success -> ForgotPasswordSuccess(email = email, onDismiss = onDismiss)
            is ForgotPasswordState.Error -> ForgotPasswordError(
                errorMessage = forgotPasswordState.message,
                onTryAgain = { onSendResetEmail(email) },
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun ForgotPasswordForm(
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String,
    onSendResetEmail: () -> Unit,
    onCancel: () -> Unit
) {
    val tokens = AppTokens.current()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = tokens.typography.title.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(tokens.spacing.m))

        Text(
            text = "Enter your email address and we'll send you a link to reset your password.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = tokens.typography.body.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(tokens.spacing.l))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = {
                Text(
                    text = "Email",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = tokens.typography.label.sp
                    )
                )
            },
            isError = emailError.isNotEmpty(),
            shape = RoundedCornerShape(tokens.corners.small),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = if (emailError.isNotEmpty())
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
            }
        )

        AnimatedVisibility(
            visible = emailError.isNotEmpty(),
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Text(
                text = emailError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = tokens.typography.caption.sp
                ),
                modifier = Modifier.padding(start = tokens.spacing.s, top = tokens.spacing.xs)
            )
        }

        Spacer(modifier = Modifier.height(tokens.spacing.l))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(tokens.spacing.s)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(tokens.corners.small)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = onSendResetEmail,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(tokens.corners.small)
            ) {
                Text("Send Link")
            }
        }
    }
}

@Composable
private fun ForgotPasswordLoading() {
    val tokens = AppTokens.current()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = tokens.spacing.xl)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(tokens.components.iconLarge),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = tokens.strokeWidths.medium
        )

        Spacer(modifier = Modifier.height(tokens.spacing.l))

        Text(
            text = "Sending reset email...",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = tokens.typography.body.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ForgotPasswordSuccess(
    email: String,
    onDismiss: () -> Unit
) {
    val tokens = AppTokens.current()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(tokens.components.imageSizeMedium)
                .padding(tokens.spacing.s),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(tokens.components.imageSizeMedium),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(tokens.spacing.m))

        Text(
            text = "Email Sent!",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = tokens.typography.title.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(tokens.spacing.s))

        Text(
            text = "We've sent a password reset link to:",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = tokens.typography.body.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(tokens.spacing.xs))

        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = tokens.typography.body.sp,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(tokens.spacing.m))

        Text(
            text = "Check your inbox and follow the instructions to reset your password.",
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = tokens.typography.caption.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = tokens.spacing.s)
        )

        Spacer(modifier = Modifier.height(tokens.spacing.l))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(tokens.corners.small)
        ) {
            Text("Done")
        }
    }
}

@Composable
private fun ForgotPasswordError(
    errorMessage: String,
    onTryAgain: () -> Unit,
    onDismiss: () -> Unit
) {
    val tokens = AppTokens.current()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = tokens.typography.title.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(tokens.spacing.m))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = tokens.typography.body.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(tokens.spacing.l))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(tokens.spacing.s)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(tokens.corners.small)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = onTryAgain,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(tokens.corners.small)
            ) {
                Text("Try Again")
            }
        }
    }
}

private fun validateEmail(email: String): String {
    return when {
        email.isBlank() -> "Email cannot be empty"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Please enter a valid email"
        else -> ""
    }
}
