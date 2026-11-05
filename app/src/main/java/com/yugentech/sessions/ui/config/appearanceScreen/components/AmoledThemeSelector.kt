package com.yugentech.sessions.ui.config.appearanceScreen.components

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.DarkMode
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.theme.viewmodel.ThemeViewModel
import com.yugentech.sessions.theme.LocalThemeReveal
import com.yugentech.sessions.theme.SLOW_FADE_MS
import com.yugentech.sessions.theme.config.ThemeMode
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.mainScreen.components.pressClickable
import org.koin.androidx.compose.koinViewModel
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.sin

private val TAU = (2 * PI).toFloat()
private const val STARS = 26
// The turn-off easing played backwards in time, for turning on
private val NightFallEasing = Easing { 1f - FastOutSlowInEasing.transform(1f - it) }
// Roughly where the switch's centre sits from the card's end; the night spreads from there
private val SWITCH_CENTRE_FROM_END = 44.dp

/**
 * AMOLED black as a card that is its own preview: on, it fades to pure black outward from
 * the switch, a field of stars twinkles in behind the text and a soft glow rises along the
 * bottom. Always shown, but it only works in dark mode.
 */
@Composable
fun AmoledThemeSelector(
    modifier: Modifier = Modifier,
    viewModel: ThemeViewModel = koinViewModel()
) {
    val themeConfig by viewModel.themeConfiguration.collectAsStateWithLifecycle()
    val isSystemDark = isSystemInDarkTheme()
    val isDarkThemeActive =
        themeConfig.themeMode == ThemeMode.DARK || (themeConfig.themeMode == ThemeMode.SYSTEM && isSystemDark)
    val haptic = LocalHapticFeedback.current
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(MaterialTheme.corners.extraLarge)
    val isOn = themeConfig.isAmoledMode && isDarkThemeActive

    // 0 = the usual surface, 1 = pure black night
    val night by animateFloatAsState(
        targetValue = if (isOn) 1f else 0f,
        // Turning off sweeps the black back into the switch; turning on plays that same sweep
        // in reverse, so both directions are one motion
        animationSpec = tween(durationMillis = 900, easing = if (isOn) NightFallEasing else FastOutSlowInEasing),
        label = "amoledNight"
    )
    // The stars only twinkle while there's night for them to be seen in
    val twinkle = if (isOn || night > 0f) {
        rememberInfiniteTransition(label = "amoledStars").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
            label = "amoledTwinkle"
        )
    } else {
        null
    }
    val contentAlpha by animateFloatAsState(if (isDarkThemeActive) 1f else 0.5f, label = "amoledEnabled")

    val reveal = LocalThemeReveal.current

    fun toggle(on: Boolean) {
        haptic.performHapticFeedback(if (on) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
        // A simple fade from the old look to the new, across the whole screen
        val apply = { viewModel.updateTheme(themeConfig.copy(isAmoledMode = on)) }
        reveal?.fade(apply, SLOW_FADE_MS) ?: apply()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pressClickable(shape = shape, enabled = isDarkThemeActive, pressedScale = 0.98f) {
                toggle(!themeConfig.isAmoledMode)
            }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(scheme.surfaceContainer)
            drawNight(night, twinkle?.value ?: 0f, scheme.primary)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.m),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DarkMode,
                contentDescription = null,
                tint = scheme.primary,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer { alpha = contentAlpha }
            )

            Spacer(Modifier.width(MaterialTheme.spacing.m))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer { alpha = contentAlpha }
            ) {
                Text(
                    text = "AMOLED Black",
                    style = MaterialTheme.typography.titleMedium,
                    color = scheme.onSurface
                )
                Text(
                    text = if (isDarkThemeActive) "Pure black background" else "Available in dark mode",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant
                )
            }

            Switch(
                checked = themeConfig.isAmoledMode,
                enabled = isDarkThemeActive,
                onCheckedChange = ::toggle,
                thumbContent = if (themeConfig.isAmoledMode) {
                    {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize)
                        )
                    }
                } else null
            )
        }
    }
}

/**
 * Pure black spreading out from the switch as [night] runs 0 → 1, with a glow along the
 * bottom and stars that show once the black has reached them, each twinkling at its own pace.
 */
private fun DrawScope.drawNight(night: Float, twinkle: Float, glow: Color) {
    if (night <= 0f) return
    val origin = Offset(size.width - SWITCH_CENTRE_FROM_END.toPx(), size.height / 2)
    // The far corners are on the start side, away from the switch
    val radius = hypot(origin.x, size.height / 2) * night
    drawCircle(Color.Black, radius, origin)

    drawRect(
        Brush.radialGradient(
            listOf(glow.copy(alpha = 0.30f * night), Color.Transparent),
            center = Offset(size.width * 0.30f, size.height * 1.25f),
            radius = size.width * 0.55f
        )
    )

    val edge = 16.dp.toPx()
    repeat(STARS) { i ->
        // An evenly spread but irregular-looking scatter, the same every time
        val star = Offset(fraction(0.13f + i * 0.7548777f) * size.width, fraction(0.29f + i * 0.5698403f) * size.height)
        val shown = ((radius - (star - origin).getDistance()) / edge).coerceIn(0f, 1f)
        if (shown <= 0f) return@repeat
        // Whole turns per loop, so the twinkle never jumps when it wraps
        val turns = 1 + i % 3
        val shine = 0.35f + 0.65f * (0.5f + 0.5f * sin((twinkle * turns + i * 0.37f) * TAU))
        val white = Color.White.copy(alpha = shown * shine)
        val r = (0.8f + (i % 4) * 0.3f).dp.toPx()
        drawCircle(white, r, star)
        // Every few stars is a brighter sparkle
        if (i % 6 == 0) {
            val arm = r * 3.5f
            val stroke = 0.8.dp.toPx()
            drawLine(white, star - Offset(arm, 0f), star + Offset(arm, 0f), stroke, StrokeCap.Round)
            drawLine(white, star - Offset(0f, arm), star + Offset(0f, arm), stroke, StrokeCap.Round)
        }
    }
}

private fun fraction(x: Float): Float = x - floor(x)
