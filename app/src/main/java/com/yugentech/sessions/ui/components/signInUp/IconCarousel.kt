package com.yugentech.sessions.ui.components.signInUp

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.ui.Tokens
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun IconCarousel(
    modifier: Modifier = Modifier
) {
    val tokens = Tokens
    val icons = listOf(
        Icons.Filled.School,
        Icons.Filled.Work,
        Icons.Filled.Lightbulb,
        Icons.AutoMirrored.Filled.Assignment
    )

    // Animation
    val infiniteTransition = rememberInfiniteTransition(label = "carousel")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 20000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Use tokens for sizing
    val circleSize = tokens.components.imageSizeLarge
    val iconSize = tokens.components.iconMediumLarge
    val centerIconSize = tokens.components.iconLarge

    val density = LocalDensity.current

    Box(
        modifier = modifier
            .size(circleSize)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // Orbiting icons
        icons.forEachIndexed { index, icon ->
            val iconAngle = angle + (360f / icons.size * index)
            val radiusPx = with(density) { circleSize.toPx() / 2f }

            val x = (radiusPx * cos(Math.toRadians(iconAngle.toDouble()))).toFloat()
            val y = (radiusPx * sin(Math.toRadians(iconAngle.toDouble()))).toFloat()

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .offset(
                        x = with(density) { x.toDp() },
                        y = with(density) { y.toDp() }
                    ),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Center icon
        Icon(
            imageVector = Icons.Filled.Timer,
            contentDescription = null,
            modifier = Modifier.size(centerIconSize),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}