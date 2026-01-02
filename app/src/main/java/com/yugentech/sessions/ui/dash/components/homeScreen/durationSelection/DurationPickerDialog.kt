package com.yugentech.sessions.ui.dash.components.homeScreen.durationSelection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
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
import com.yugentech.sessions.ui.dash.components.common.ExpressiveSlider

@Composable
fun DurationPickerDialog(
    title: String,
    initialValue: Int,
    range: IntRange,
    step: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    // Current value state
    var sliderValue by remember { mutableIntStateOf(initialValue) }

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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Big Value Display
                Text(
                    text = "$sliderValue min",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                // 2. The Wavy Slider
                ExpressiveSlider(
                    value = sliderValue.toFloat(),
                    onValueChange = { newValue ->
                        // Logic to snap to steps manually
                        val snapped = Math.round(newValue / step) * step
                        sliderValue = snapped
                    },
                    valueRange = range.first.toFloat()..range.last.toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )

                // Helper Labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${range.first}m",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "${range.last}m",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(sliderValue) }) {
                Text("Set Time")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}