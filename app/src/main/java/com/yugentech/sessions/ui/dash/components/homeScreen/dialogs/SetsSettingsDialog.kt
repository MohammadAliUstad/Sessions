package com.yugentech.sessions.ui.dash.components.homeScreen.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SetsSettingsDialog(
    currentSets: Int,
    currentLongBreak: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var sets by remember { mutableIntStateOf(currentSets) }
    var longBreak by remember { mutableIntStateOf(currentLongBreak) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Session Goals"
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Target Sets",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            "$sets",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = sets.toFloat(),
                        onValueChange = { sets = it.toInt() },
                        valueRange = 1f..10f,
                        steps = 8
                    )
                }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Long Break",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            "${longBreak}m",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Slider(
                        value = longBreak.toFloat(),
                        onValueChange = { longBreak = it.toInt() },
                        valueRange = 5f..60f,
                        steps = 10
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(sets, longBreak)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}