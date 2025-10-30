package com.yugentech.sessions.ui.components.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.ui.Tokens // Added import

@Composable
fun DurationSelector(
    selectedDuration: Int,
    availableDurations: List<Int>,
    onDurationSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = Tokens

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(tokens.corners.large) // Use token
    ) {
        Column(
            modifier = Modifier.padding(tokens.spacing.m) // Use token
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = tokens.spacing.sm) // Use token
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Session Duration",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(tokens.components.iconMediumSmall) // Use token
                )
                Spacer(modifier = Modifier.width(tokens.spacing.s)) // Use token
                Text(
                    text = "Duration",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(tokens.corners.medium) // Use token
                    )
                    .padding(tokens.spacing.xs), // Use token
                horizontalArrangement = Arrangement.spacedBy(tokens.spacing.xs) // Use token
            ) {
                availableDurations.forEach { min ->
                    val isSelected = selectedDuration == (min * 60)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(tokens.components.buttonSmall) // Use token
                            .clip(RoundedCornerShape(tokens.corners.smallMedium)) // Use token
                            .background(
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                            .clickable { onDurationSelected(min) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$min min",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                            ),
                            color = if (isSelected)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}