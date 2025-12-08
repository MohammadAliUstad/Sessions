package com.yugentech.sessions.ui.auth.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.yugentech.sessions.theme.tokens.animations
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.dimensions.AnimationLabels
import com.yugentech.sessions.theme.tokens.dimensions.AppConstants
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
        label = AnimationLabels.CAROUSEL
    )

    val angle by infiniteTransition.animateFloat(
        initialValue = AppConstants.ZEROF,
        targetValue = AppConstants.THREESIXTYF,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = MaterialTheme.animations.durations.carousel,
                easing = MaterialTheme.animations.easings.linear
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = AnimationLabels.ROTATION_ANIMATION
    )

    val circleSize = MaterialTheme.components.imageSizeLarge
    val orbitingIconSize = MaterialTheme.icons.extraLarge
    val centerIconSize = MaterialTheme.icons.large
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
        icons.forEachIndexed { index, icon ->
            val segment = AppConstants.THREESIXTYF / icons.size
            val animatedAngle = angle + (segment * index)

            val radiusPx = with(density) { circleSize.toPx() / AppConstants.TWOF }

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