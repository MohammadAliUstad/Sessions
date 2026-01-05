package com.yugentech.sessions.ui.dash.components.homeScreen.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderDefaults
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
import ir.mahozad.multiplatform.wavyslider.material3.WavySlider
import ir.mahozad.multiplatform.wavyslider.WaveDirection
import kotlin.math.roundToInt

@Composable
fun DurationPickerDialog(
    title: String,
    initialValue: Int,
    range: IntRange,
    step: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
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
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. Large Value Display
                Text(
                    text = "$sliderValue min",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFeatureSettings = "tnum"
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                // 2. The Wavy Slider (Input)
                Column {
                    WavySlider(
                        value = sliderValue.toFloat(),
                        onValueChange = { newValue ->
                            // Snap logic: Round to nearest 'step'
                            val snapped = (newValue / step).roundToInt() * step
                            sliderValue = snapped
                        },
                        valueRange = range.first.toFloat()..range.last.toFloat(),
                        modifier = Modifier.fillMaxWidth(),
                        // EXPRESSIVE STYLING:
                        waveHeight = 10.dp,
                        waveLength = 30.dp,
                        waveVelocity = 10.dp to WaveDirection.TAIL,

                        // FIX IS HERE: Use SliderDefaults.colors() instead of direct params
                        colors = SliderDefaults.colors(
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                            thumbColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    // Range Labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${range.first}m",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "${range.last}m",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}