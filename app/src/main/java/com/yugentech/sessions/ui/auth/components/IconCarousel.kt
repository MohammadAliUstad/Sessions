package com.yugentech.sessions.ui.auth.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.icons
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun IconCarousel(
    modifier: Modifier = Modifier
) {
    val icons = listOf(
        Icons.Filled.School,
        Icons.Filled.Work,
        Icons.Filled.Lightbulb,
        Icons.AutoMirrored.Filled.Assignment
    )

    val infiniteTransition = rememberInfiniteTransition(
        label = "carousel"
    )

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = AppAnimations.Durations.Carousel,
                easing = AppAnimations.Easings.Linear
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val boundaryPadding = MaterialTheme.icons.extraLarge / 2
    val circleSize = MaterialTheme.components.imageSizeLarge
    val orbitingIconSize = MaterialTheme.icons.extraLarge
    val centerIconSize = MaterialTheme.icons.large
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .size(circleSize)
            .padding(boundaryPadding)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        icons.forEachIndexed { index, icon ->
            val segment = 360f / icons.size
            val animatedAngle = angle + (segment * index)

            val radiusPx = with(density) { circleSize.toPx() / 2f }

            val x = (radiusPx * cos(Math.toRadians(animatedAngle.toDouble()))).toFloat()
            val y = (radiusPx * sin(Math.toRadians(animatedAngle.toDouble()))).toFloat()

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(orbitingIconSize)
                    .offset(
                        x = with(density) { x.toDp() },
                        y = with(density) { y.toDp() }
                    ),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Icon(
            imageVector = Icons.Filled.Timer,
            contentDescription = null,
            modifier = Modifier.size(centerIconSize),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}