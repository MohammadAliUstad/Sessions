package com.yugentech.sessions.ui.config.appearanceScreen.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.theme.config.HomeLayout
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.theme.viewmodel.ThemeViewModel
import com.yugentech.sessions.ui.dash.mainScreen.components.pressClickable
import org.koin.androidx.compose.koinViewModel

/**
 * Toggle that shows or hides the living colour-wash gradient at the top of the home screen.
 * Only applies to Refined and Studio layouts; it is greyed out when Classic is active.
 */
@Composable
fun HomeGradientSelector(
    modifier: Modifier = Modifier,
    viewModel: ThemeViewModel = koinViewModel()
) {
    val themeConfig by viewModel.themeConfiguration.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(MaterialTheme.corners.extraLarge)
    val fadeThrough = AppAnimations.Transitions.rememberExpressiveFadeThrough()

    val enabled = themeConfig.homeLayout != HomeLayout.CLASSIC
    val isOn = themeConfig.showHomeGradient && enabled

    // The glow behind the row fades in when the gradient is on and fades out cleanly when off or disabled
    val glowAlpha by animateFloatAsState(
        targetValue = if (isOn) 1f else 0f,
        animationSpec = tween(if (isOn) 900 else 400, easing = FastOutSlowInEasing),
        label = "gradientGlowAlpha"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.45f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "gradientContentAlpha"
    )

    fun toggle(on: Boolean) {
        if (!enabled) return
        haptic.performHapticFeedback(if (on) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
        viewModel.updateTheme(themeConfig.copy(showHomeGradient = on))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pressClickable(shape = shape, enabled = enabled, pressedScale = 0.98f) {
                toggle(!themeConfig.showHomeGradient)
            }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(scheme.surfaceContainer)
            if (glowAlpha > 0f) {
                // A soft horizontal wash of the theme's three accent colours, like the real gradient
                drawRect(
                    Brush.horizontalGradient(
                        listOf(
                            scheme.primary.copy(alpha = 0.22f * glowAlpha),
                            scheme.tertiary.copy(alpha = 0.16f * glowAlpha),
                            scheme.secondary.copy(alpha = 0.10f * glowAlpha)
                        )
                    )
                )
                // A faint vertical fade so the glow feels like it's coming from the top
                drawRect(
                    Brush.verticalGradient(
                        listOf(
                            scheme.primary.copy(alpha = 0.14f * glowAlpha),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = size.height
                    )
                )
                // Small soft radial glow in the top-left corner, echoing AliveGradient's feel
                val glow = Offset(size.width * 0.15f, 0f)
                drawCircle(
                    Brush.radialGradient(
                        listOf(scheme.tertiary.copy(alpha = 0.28f * glowAlpha), Color.Transparent),
                        center = glow,
                        radius = size.width * 0.45f
                    ),
                    radius = size.width * 0.45f,
                    center = glow
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.m)
                .graphicsLayer { alpha = contentAlpha },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Flare,
                contentDescription = null,
                tint = scheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.width(MaterialTheme.spacing.m))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Home Gradient",
                    style = MaterialTheme.typography.titleMedium,
                    color = scheme.onSurface
                )
                AnimatedContent(
                    targetState = if (enabled) "Colour wash at the top of the home screen" else "Available in Refined and Studio layouts",
                    transitionSpec = { fadeThrough() },
                    label = "gradientSubtitleTransition"
                ) { targetText ->
                    Text(
                        text = targetText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant
                    )
                }
            }

            Switch(
                checked = themeConfig.showHomeGradient,
                enabled = enabled,
                onCheckedChange = ::toggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    checkedIconColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedIconColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledCheckedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledCheckedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledUncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledCheckedIconColor = MaterialTheme.colorScheme.primaryContainer,
                    disabledUncheckedIconColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                thumbContent = {
                    Icon(
                        imageVector = if (themeConfig.showHomeGradient) Icons.Default.Flare else Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize)
                    )
                }
            )
        }
    }
}
