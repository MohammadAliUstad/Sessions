package com.yugentech.sessions.ui.config.appearanceScreen.components

import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.theme.config.HomeLayout
import com.yugentech.sessions.theme.viewmodel.ThemeViewModel
import com.yugentech.sessions.ui.dash.mainScreen.components.SectionHeader
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import org.koin.androidx.compose.koinViewModel

/** Classic, Refined and Studio as three small sketches of Home in the current colours; tap one to use it. */
@Composable
fun HomeLayoutSelector(
    modifier: Modifier = Modifier,
    viewModel: ThemeViewModel = koinViewModel()
) {
    val themeConfig by viewModel.themeConfiguration.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val scheme = MaterialTheme.colorScheme
    // Moves on the tap itself, not when the saved setting comes back, so the outline starts at once
    var chosen by remember(themeConfig.homeLayout) { mutableStateOf(themeConfig.homeLayout) }

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(icon = Icons.Default.SpaceDashboard, title = stringResource(R.string.home_layout_title))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HomeLayout.entries.forEach { layout ->
                val selected = layout == chosen
                val shape = RoundedCornerShape(20.dp)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(shape)
                        // No ripple and no press scale: the outline is the only feedback
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!selected) {
                                haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                chosen = layout
                                viewModel.updateTheme(themeConfig.copy(homeLayout = layout))
                            }
                        }
                        .background(scheme.surfaceContainer)
                        .crossfadeSelectionOutline(
                            isSelected = selected,
                            shape = shape,
                            unselectedColor = scheme.outlineVariant.copy(alpha = 0.5f)
                        )
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Canvas(modifier = Modifier.fillMaxWidth().aspectRatio(0.62f)) {
                        drawPhone(scheme)
                        when (layout) {
                            HomeLayout.CLASSIC -> drawClassic(scheme)
                            HomeLayout.REFINED -> drawRefined(scheme)
                            HomeLayout.STUDIO -> drawStudio(scheme)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(layout.label),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = animateSelectionColor(selected, scheme.onSurface, scheme.onSurfaceVariant),
                        maxLines = 1
                    )
                    // Always two lines tall, wrapped or not, so the three tiles stay the same height
                    Text(
                        text = stringResource(layout.description),
                        style = MaterialTheme.typography.labelSmall,
                        color = scheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        minLines = 2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ───────────── Sketches: all in fractions of the phone, so they scale with the tile ─────────────

private fun DrawScope.drawPhone(scheme: ColorScheme) {
    val r = CornerRadius(size.width * 0.14f)
    drawRoundRect(scheme.background, cornerRadius = r)
    drawRoundRect(scheme.outlineVariant, cornerRadius = r, style = Stroke(1.dp.toPx()))
}

private fun DrawScope.bar(x: Float, y: Float, w: Float, h: Float, color: Color) {
    drawRoundRect(
        color,
        topLeft = Offset(size.width * x, size.height * y),
        size = Size(size.width * w, size.height * h),
        cornerRadius = CornerRadius(size.height * h / 2f)
    )
}

private fun DrawScope.block(x: Float, y: Float, w: Float, h: Float, color: Color, radius: Float = 0.06f) {
    drawRoundRect(
        color,
        topLeft = Offset(size.width * x, size.height * y),
        size = Size(size.width * w, size.height * h),
        cornerRadius = CornerRadius(size.width * radius)
    )
}

private fun DrawScope.ring(cy: Float, radius: Float, track: Color, fill: Color) {
    val c = Offset(size.width / 2f, size.height * cy)
    val r = size.width * radius
    val stroke = Stroke(size.width * 0.035f, cap = StrokeCap.Round)
    drawCircle(track, r, c, style = stroke)
    drawArc(fill, -90f, 220f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = stroke)
}

private fun DrawScope.drawClassic(s: ColorScheme) {
    // Task chip and two badges
    block(0.08f, 0.06f, 0.48f, 0.07f, s.surfaceContainerHigh)
    block(0.60f, 0.06f, 0.15f, 0.07f, s.surfaceContainerHigh)
    block(0.78f, 0.06f, 0.14f, 0.07f, s.surfaceContainerHigh)
    ring(0.36f, 0.30f, s.surfaceVariant, s.primary)
    // Two coloured config blocks
    block(0.08f, 0.62f, 0.40f, 0.15f, s.primaryContainer)
    block(0.52f, 0.62f, 0.40f, 0.15f, s.tertiaryContainer)
    // Button group
    block(0.14f, 0.84f, 0.18f, 0.09f, s.secondaryContainer, 0.08f)
    block(0.36f, 0.84f, 0.28f, 0.09f, s.primary, 0.08f)
    block(0.68f, 0.84f, 0.18f, 0.09f, s.secondaryContainer, 0.08f)
}

private fun DrawScope.drawRefined(s: ColorScheme) {
    // Status line over the task, sound pill
    bar(0.10f, 0.06f, 0.28f, 0.02f, s.onSurfaceVariant.copy(alpha = 0.5f))
    bar(0.10f, 0.10f, 0.50f, 0.035f, s.onSurface.copy(alpha = 0.8f))
    bar(0.68f, 0.07f, 0.22f, 0.06f, s.surfaceContainerHigh)
    // Ring with a soft glow behind it
    val c = Offset(size.width / 2f, size.height * 0.37f)
    drawCircle(Brush.radialGradient(listOf(s.primary.copy(alpha = 0.2f), Color.Transparent), c, size.width * 0.4f), size.width * 0.4f, c)
    ring(0.37f, 0.29f, s.surfaceContainerHighest, s.primary)
    // One plan card with its summary strip
    block(0.08f, 0.62f, 0.84f, 0.16f, s.surfaceContainer)
    bar(0.20f, 0.73f, 0.60f, 0.03f, s.surfaceContainerHighest)
    // Button group
    block(0.14f, 0.84f, 0.18f, 0.09f, s.secondaryContainer, 0.08f)
    block(0.36f, 0.84f, 0.28f, 0.09f, s.primary, 0.08f)
    block(0.68f, 0.84f, 0.18f, 0.09f, s.secondaryContainer, 0.08f)
}

private fun DrawScope.drawStudio(s: ColorScheme) {
    // Date overline and big task heading
    bar(0.10f, 0.06f, 0.30f, 0.018f, s.onSurfaceVariant.copy(alpha = 0.5f))
    bar(0.10f, 0.095f, 0.58f, 0.04f, s.onSurface.copy(alpha = 0.85f))
    // The dial: a soft glass face, lighter at the top, a hint of colour in the middle and a lit rim
    val c = Offset(size.width / 2f, size.height * 0.38f)
    val face = size.width * 0.34f
    drawCircle(
        Brush.verticalGradient(
            listOf(s.surfaceContainerHigh.copy(alpha = 0.62f), s.surfaceContainerLowest.copy(alpha = 0.38f)),
            startY = c.y - face,
            endY = c.y + face
        ),
        face,
        c
    )
    drawCircle(Brush.radialGradient(listOf(s.primary.copy(alpha = 0.10f), Color.Transparent), c, face), face, c)
    drawCircle(
        Brush.verticalGradient(
            listOf(s.onSurface.copy(alpha = 0.22f), s.onSurface.copy(alpha = 0.05f), s.primary.copy(alpha = 0.30f)),
            startY = c.y - face,
            endY = c.y + face
        ),
        face,
        c,
        style = Stroke(size.width * 0.008f)
    )
    val outer = face * 0.86f
    for (i in 0 until 40) {
        val a = i / 40f * 2f * PI.toFloat() - PI.toFloat() / 2f
        val d = Offset(cos(a), sin(a))
        val len = if (i % 5 == 0) face * 0.16f else face * 0.09f
        drawLine(
            if (i < 15) s.primary else s.onSurface.copy(alpha = 0.15f),
            c + d * (outer - len), c + d * outer,
            strokeWidth = size.width * 0.012f, cap = StrokeCap.Round
        )
    }
    bar(0.36f, 0.365f, 0.28f, 0.035f, s.onSurface.copy(alpha = 0.7f))
    // Three plan tiles
    block(0.08f, 0.64f, 0.26f, 0.10f, s.surfaceContainer)
    block(0.37f, 0.64f, 0.26f, 0.10f, s.surfaceContainer)
    block(0.66f, 0.64f, 0.26f, 0.10f, s.surfaceContainer)
    // The floating dock with its wide main pill, in the same colours as the real one
    block(0.06f, 0.82f, 0.88f, 0.12f, s.surfaceContainerHigh, 0.12f)
    block(0.09f, 0.835f, 0.16f, 0.09f, s.surfaceContainerHighest, 0.07f)
    block(0.28f, 0.835f, 0.44f, 0.09f, s.primary, 0.09f)
    block(0.75f, 0.835f, 0.16f, 0.09f, s.surfaceContainerHighest, 0.07f)
}
