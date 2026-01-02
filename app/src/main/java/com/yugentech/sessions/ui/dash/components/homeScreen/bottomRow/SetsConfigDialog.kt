package com.yugentech.sessions.ui.dash.components.homeScreen.bottomRow

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.ui.dash.components.common.SmallStepper

@Composable
fun SetsConfigDialog(
    currentRounds: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var rounds by remember { mutableIntStateOf(currentRounds) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Session Goal") }, // Renamed from "Long Break Settings"
        text = {
            Column {
                Text(
                    "How many sets do you want to complete?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Stepper for Rounds ONLY
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Total Sets", style = MaterialTheme.typography.titleMedium)
                    SmallStepper(value = rounds, onValueChange = { rounds = it }, range = 1..10)
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(rounds) }) { Text("Set Goal") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}