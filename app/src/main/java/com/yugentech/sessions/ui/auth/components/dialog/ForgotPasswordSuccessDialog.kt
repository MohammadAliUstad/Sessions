package com.yugentech.sessions.ui.auth.components.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MarkEmailRead
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yugentech.sessions.R
import com.yugentech.sessions.ui.components.SessionsDialog

@Composable
fun ForgotPasswordSuccessDialog(
    onDismiss: () -> Unit
) {
    SessionsDialog(
        icon = Icons.Outlined.MarkEmailRead,
        title = stringResource(R.string.forgot_password_sent_title),
        message = stringResource(R.string.forgot_password_sent_message),
        primaryLabel = stringResource(R.string.ok),
        onPrimary = onDismiss,
        onDismiss = onDismiss
    )
}
