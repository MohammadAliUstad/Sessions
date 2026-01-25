package com.yugentech.sessions.ui.dash.components.settingsScreen

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.core.net.toUri
import com.yugentech.sessions.theme.tokens.corners

@Composable
fun AlarmPermissionDialog(
    context: Context,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Permission Required") },
        text = {
            Text(
                text = "To ensure your study reminder rings at the exact time you set, please allow the permission 'Alarms & Reminders' in the next screen.",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        shape = RoundedCornerShape(MaterialTheme.corners.large), // 24.dp
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val intent = Intent(
                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            "package:${context.packageName}".toUri()
                        )
                        context.startActivity(intent)
                    } else {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            "package:${context.packageName}".toUri()
                        )
                        context.startActivity(intent)
                    }
                }
            ) { Text(text = "Go to Settings") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}