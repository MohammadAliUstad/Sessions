package com.yugentech.sessions.ui.config.appearanceScreen.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.LocalThemeReveal
import com.yugentech.sessions.theme.builder.getColorScheme
import com.yugentech.sessions.theme.config.ThemeMode
import com.yugentech.sessions.theme.viewmodel.ThemeViewModel
import com.yugentech.sessions.ui.dash.mainScreen.components.SectionHeader
import com.yugentech.sessions.ui.dash.mainScreen.components.pressClickable
import org.koin.androidx.compose.koinViewModel

/**
 * Light, Dark and System as three tiny phones drawn in the colours each would give, System split
 * down the middle.
 */
@Composable
fun ThemeModeSelector(
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val haptic = LocalHapticFeedback.current
    val themeConfig by themeViewModel.themeConfiguration.collectAsStateWithLifecycle()
    val light = getColorScheme(themeConfig.copy(themeMode = ThemeMode.LIGHT))
    val dark = getColorScheme(themeConfig.copy(themeMode = ThemeMode.DARK))
    // Light and dark wipe in from the tapped tile; nothing else changes that much at once.
    val reveal = LocalThemeReveal.current

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            icon = Icons.Default.Brightness6,
            title = stringResource(R.string.theme_mode)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ThemeMode.entries.forEach { mode ->
                val selected = themeConfig.themeMode == mode
                var center by remember { mutableStateOf(Offset.Zero) }
                val shape = RoundedCornerShape(20.dp)
                val borderColor by animateColorAsState(
                    if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    label = "modeBorder"
                )
                val borderWidth by animateDpAsState(if (selected) 2.5.dp else 1.dp, label = "modeBorderWidth")
                val (icon, label) = when (mode) {
                    ThemeMode.LIGHT -> Icons.Default.LightMode to stringResource(R.string.light)
                    ThemeMode.DARK -> Icons.Default.DarkMode to stringResource(R.string.dark)
                    ThemeMode.SYSTEM -> Icons.Default.AutoMode to stringResource(R.string.system)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .onGloballyPositioned { center = it.boundsInRoot().center }
                        .pressClickable(shape = shape, pressedScale = 0.95f) {
                            if (!selected) {
                                haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                val apply = { themeViewModel.updateTheme(themeConfig.copy(themeMode = mode)) }
                                reveal?.reveal(center, apply) ?: apply()
                            }
                        }
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .border(borderWidth, borderColor, shape)
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Canvas(modifier = Modifier.fillMaxWidth().aspectRatio(0.72f)) {
                        when (mode) {
                            ThemeMode.LIGHT -> drawMiniPhone(light)
                            ThemeMode.DARK -> drawMiniPhone(dark)
                            ThemeMode.SYSTEM -> {
                                drawMiniPhone(light)
                                clipRect(left = size.width / 2f) { drawMiniPhone(dark) }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/** A sketch of a screen: a hero card, three rows and the nav pill, in [scheme]'s colours. */
private fun DrawScope.drawMiniPhone(scheme: ColorScheme) {
    val w = size.width
    val h = size.height
    val pad = w * 0.09f
    val inner = w - pad * 2

    drawRoundRect(scheme.background, cornerRadius = CornerRadius(w * 0.16f))
    drawRoundRect(
        color = scheme.outlineVariant,
        cornerRadius = CornerRadius(w * 0.16f),
        style = Stroke(width = 1.dp.toPx())
    )

    // Hero card with two "text" bars
    val heroTop = pad * 1.6f
    val heroHeight = h * 0.28f
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(scheme.primary, lerp(scheme.primary, Color.Black, 0.28f)),
            start = Offset(pad, heroTop),
            end = Offset(pad + inner, heroTop + heroHeight)
        ),
        topLeft = Offset(pad, heroTop),
        size = Size(inner, heroHeight),
        cornerRadius = CornerRadius(w * 0.09f)
    )
    val bar = h * 0.035f
    drawRoundRect(
        color = scheme.onPrimary.copy(alpha = 0.55f),
        topLeft = Offset(pad * 1.8f, heroTop + heroHeight * 0.22f),
        size = Size(inner * 0.30f, bar),
        cornerRadius = CornerRadius(bar / 2)
    )
    drawRoundRect(
        color = scheme.onPrimary.copy(alpha = 0.9f),
        topLeft = Offset(pad * 1.8f, heroTop + heroHeight * 0.48f),
        size = Size(inner * 0.55f, bar * 1.6f),
        cornerRadius = CornerRadius(bar)
    )

    // Rows: a badge and a line each
    val rowHeight = h * 0.11f
    val gap = h * 0.03f
    repeat(3) { i ->
        val top = heroTop + heroHeight + pad + i * (rowHeight + gap)
        drawRoundRect(
            color = scheme.surfaceContainer,
            topLeft = Offset(pad, top),
            size = Size(inner, rowHeight),
            cornerRadius = CornerRadius(rowHeight * 0.35f)
        )
        drawCircle(
            color = scheme.primary.copy(alpha = 0.75f),
            radius = rowHeight * 0.26f,
            center = Offset(pad + rowHeight * 0.5f, top + rowHeight / 2)
        )
        drawRoundRect(
            color = scheme.onSurfaceVariant.copy(alpha = 0.45f),
            topLeft = Offset(pad + rowHeight, top + rowHeight / 2 - bar / 2),
            size = Size(inner * (0.45f - 0.08f * i), bar),
            cornerRadius = CornerRadius(bar / 2)
        )
    }

    // Bottom nav pill
    val navHeight = h * 0.07f
    drawRoundRect(
        color = scheme.surfaceContainerHigh,
        topLeft = Offset(pad * 1.5f, h - pad - navHeight),
        size = Size(w - pad * 3, navHeight),
        cornerRadius = CornerRadius(navHeight / 2)
    )
}
