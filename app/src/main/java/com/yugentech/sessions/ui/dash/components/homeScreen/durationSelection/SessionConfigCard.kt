package com.yugentech.sessions.ui.dash.components.homeScreen.durationSelection

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SessionConfigCard(
    focusDurationMillis: Long,
    shortBreakDurationMillis: Long,
    onFocusClick: () -> Unit,
    onShortBreakClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp) // MATCHED: Now 160.dp to align with ProgressCard
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConfigItem(
                label = "Focus Duration",
                value = "${focusDurationMillis / 60000}",
                icon = Icons.Outlined.Timer,
                onClick = onFocusClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )

            ConfigItem(
                label = "Short Break",
                value = "${shortBreakDurationMillis / 60000}",
                icon = Icons.Outlined.Coffee,
                onClick = onShortBreakClick,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ConfigItem(
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .scale(scale)
            .clip(RoundedCornerShape(24.dp)) // Matched corner radius to ProgressCard
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        color = containerColor,
        contentColor = contentColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Watermark Icon
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 20.dp, y = (-5).dp) // Slight adjustment for the shorter card
                    .size(100.dp)
                    .rotate(-15f),
                tint = contentColor.copy(alpha = 0.15f)
            )

            // Content
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = contentColor.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = contentColor
                    )

                    Text(
                        text = "min",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor.copy(alpha = 0.6f),
                        modifier = Modifier
                            .padding(bottom = 6.dp, start = 4.dp)
                    )
                }
            }
        }
    }
}