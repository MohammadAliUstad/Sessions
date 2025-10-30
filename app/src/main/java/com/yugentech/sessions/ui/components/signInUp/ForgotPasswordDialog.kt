package com.yugentech.sessions.ui.components.signInUp

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.yugentech.sessions.ui.Tokens
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
    val tokens = Tokens
    var email by remember { mutableStateOf(initialEmail) }
    var emailError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            // Correct token usage
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
            is ForgotPasswordState.Success -> ForgotPasswordSuccess(
                email = email,
                onDismiss = onDismiss
            )

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
    val tokens = Tokens

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Reset Password",
            // MODIFIED: Removed 'fontSize = tokens.typography.title.sp'
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        // Correct token usage
        Spacer(modifier = Modifier.height(tokens.spacing.m))

        Text(
            text = "Enter your email address and we'll send you a link to reset your password.",
            // MODIFIED: Removed 'fontSize = tokens.typography.body.sp'
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Correct token usage
        Spacer(modifier = Modifier.height(tokens.spacing.l))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = {
                Text(
                    text = "Email",
                    // MODIFIED: Removed 'fontSize = tokens.typography.label.sp'
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            isError = emailError.isNotEmpty(),
            // Correct token usage
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
                // MODIFIED: Removed 'fontSize = tokens.typography.caption.sp'
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = tokens.spacing.s, top = tokens.spacing.xs)
            )
        }

        // Correct token usage
        Spacer(modifier = Modifier.height(tokens.spacing.l))

        Row(
            modifier = Modifier.fillMaxWidth(),
            // Correct token usage
            horizontalArrangement = Arrangement.spacedBy(tokens.spacing.s)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                // Correct token usage
                shape = RoundedCornerShape(tokens.corners.small)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = onSendResetEmail,
                modifier = Modifier.weight(1f),
                // Correct token usage
                shape = RoundedCornerShape(tokens.corners.small)
            ) {
                Text("Send Link")
            }
        }
    }
}

@Composable
private fun ForgotPasswordLoading() {
    val tokens = Tokens

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            // Correct token usage
            .padding(vertical = tokens.spacing.xl)
    ) {
        CircularProgressIndicator(
            // Correct token usage
            modifier = Modifier.size(tokens.components.iconLarge),
            color = MaterialTheme.colorScheme.primary,
            // Correct token usage
            strokeWidth = tokens.strokeWidths.medium
        )

        // Correct token usage
        Spacer(modifier = Modifier.height(tokens.spacing.l))

        Text(
            text = "Sending reset email...",
            // MODIFIED: Removed 'fontSize = tokens.typography.body.sp'
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ForgotPasswordSuccess(
    email: String,
    onDismiss: () -> Unit
) {
    val tokens = Tokens

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                // Correct token usage
                .size(tokens.components.imageSizeMedium)
                .padding(tokens.spacing.s),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                // Correct token usage
                modifier = Modifier.size(tokens.components.imageSizeMedium),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Correct token usage
        Spacer(modifier = Modifier.height(tokens.spacing.m))

        Text(
            text = "Email Sent!",
            // MODIFIED: Removed 'fontSize = tokens.typography.title.sp'
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        // Correct token usage
        Spacer(modifier = Modifier.height(tokens.spacing.s))

        Text(
            text = "We've sent a password reset link to:",
            // MODIFIED: Removed 'fontSize = tokens.typography.body.sp'
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        // Correct token usage
        Spacer(modifier = Modifier.height(tokens.spacing.xs))

        Text(
            text = email,
            // MODIFIED: Removed 'fontSize = tokens.typography.body.sp'
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        // Correct token usage
        Spacer(modifier = Modifier.height(tokens.spacing.m))

        Text(
            text = "Check your inbox and follow the instructions to reset your password.",
            // MODIFIED: Removed 'fontSize = tokens.typography.caption.sp'
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            // Correct token usage
            modifier = Modifier.padding(horizontal = tokens.spacing.s)
        )

        // Correct token usage
        Spacer(modifier = Modifier.height(tokens.spacing.l))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            // Correct token usage
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
    val tokens = Tokens

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Error",
            // MODIFIED: Removed 'fontSize = tokens.typography.title.sp'
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.error
        )

        // Correct token usage
        Spacer(modifier = Modifier.height(tokens.spacing.m))

        Text(
            text = errorMessage,
            // MODIFIED: Removed 'fontSize = tokens.typography.body.sp'
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Correct token usage
        Spacer(modifier = Modifier.height(tokens.spacing.l))

        Row(
            modifier = Modifier.fillMaxWidth(),
            // Correct token usage
            horizontalArrangement = Arrangement.spacedBy(tokens.spacing.s)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                // Correct token usage
                shape = RoundedCornerShape(tokens.corners.small)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = onTryAgain,
                modifier = Modifier.weight(1f),
                // Correct token usage
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