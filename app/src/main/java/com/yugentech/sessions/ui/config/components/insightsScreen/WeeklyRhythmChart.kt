package com.yugentech.sessions.ui.config.components.insightsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun WeeklyRhythmChart(
    dailyVolume: Map<Int, Int>
) {
    val days = listOf("S", "M", "T", "W", "T", "F", "S")
    val maxVolume = dailyVolume.values.maxOrNull()?.coerceAtLeast(1) ?: 1

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(MaterialTheme.corners.medium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = MaterialTheme.spacing.l,
                    horizontal = MaterialTheme.spacing.s
                ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEachIndexed { index, day ->
                val count = dailyVolume[index + 1] ?: 0
                val progress = count.toFloat() / maxVolume

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .height(MaterialTheme.components.controlBarItemWidthWide)
                            .width(MaterialTheme.spacing.m) // 16.dp
                            .clip(RoundedCornerShape(MaterialTheme.corners.pill))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(progress)
                                .clip(RoundedCornerShape(MaterialTheme.corners.pill))
                                .background(
                                    if (count == maxVolume && count > 0)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                )
                        )
                    }

                    Spacer(Modifier.height(MaterialTheme.spacing.s))

                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (count == maxVolume) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}