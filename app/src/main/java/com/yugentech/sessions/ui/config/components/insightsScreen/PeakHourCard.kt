package com.yugentech.sessions.ui.config.components.insightsScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun PeakHourCard(
    peakHour: Int?
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MaterialTheme.corners.large),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(MaterialTheme.spacing.m),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (peakHour != null) {
                val period = if (peakHour < 12) "AM" else "PM"
                val displayHour = when {
                    peakHour == 0 -> 12
                    peakHour > 12 -> peakHour - 12
                    else -> peakHour
                }

                Text(
                    text = "$displayHour$period",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )

                Spacer(Modifier.width(MaterialTheme.spacing.m))

                Column {
                    Text(
                        text = "Peak Productivity",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Your most active hour",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                Text(
                    text = "--",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.5f)
                )

                Spacer(Modifier.width(MaterialTheme.spacing.m))

                Column {
                    Text(
                        text = "No Data Yet",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Log more sessions to calculate",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}