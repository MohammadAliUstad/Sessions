package com.yugentech.sessions.ui.dash.components.homeScreen.durationSelection

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.ui.dash.components.common.BigTimeStepper

@Composable
fun DurationPickerDialog(
    title: String,
    initialValue: Int,
    range: IntRange,
    step: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var currentValue by remember { mutableIntStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Set duration in minutes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                BigTimeStepper(
                    value = currentValue,
                    onValueChange = { currentValue = it },
                    range = range,
                    step = step
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(currentValue) }) { Text("Set Time") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}