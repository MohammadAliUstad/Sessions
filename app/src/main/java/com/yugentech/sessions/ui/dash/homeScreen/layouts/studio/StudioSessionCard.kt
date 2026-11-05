package com.yugentech.sessions.ui.dash.homeScreen.layouts.studio

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yugentech.sessions.theme.config.CardAnimationStyle
import com.yugentech.sessions.theme.config.CardStyle
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.HeroCardBackground
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.SessionPulse
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.heroBrush
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.heroPalette
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.heroSurface
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.rememberEffectTime
import com.yugentech.sessions.ui.dash.mainScreen.components.pressClickable
import com.yugentech.sessions.ui.dash.state.ItemStatus
import com.yugentech.sessions.ui.dash.state.SessionDashboardState
import com.yugentech.sessions.ui.dash.state.SessionVisualItem

private val CardShape = RoundedCornerShape(28.dp)

/**
 * The one height both of Studio's middle cards share, the plan before a session and this card
 * during one, so swapping between them never moves the dial above.
 */
internal val StudioCardHeight = 136.dp

/**
 * The session while it runs, painted in the chosen card style with the chosen animation behind
 * it: which set this is, the encouragement for it, and a timeline of every block and break where
 * the current block fills as it runs.
 */
@Composable
fun StudioSessionCard(
    state: SessionDashboardState,
    targetSets: Int,
    blockProgress: Float,
    isRunning: Boolean,
    pulse: SessionPulse,
    cardStyle: CardStyle,
    animationStyle: CardAnimationStyle,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = heroPalette(cardStyle)
    val time = if (animationStyle != CardAnimationStyle.NONE) rememberEffectTime(pulse.running) else null

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(StudioCardHeight)
            .heroSurface(cardStyle, heroBrush(), CardShape)
    ) {
        HeroCardBackground(
            style = animationStyle,
            time = time,
            pulse = pulse,
            ink = palette.ink,
            shade = palette.shade,
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (state.isLongBreakActive) "LONG BREAK" else "SET ${state.progressDisplay} OF $targetSets",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.8.sp,
                    color = palette.content.copy(alpha = 0.75f),
                    modifier = Modifier.weight(1f)
                )
                AnimatedVisibility(visible = targetSets > 1 && isRunning, enter = fadeIn(), exit = fadeOut()) {
                    Row(
                        modifier = Modifier
                            .pressClickable(shape = CircleShape, onClickLabel = "Skip to next", onClick = onSkip)
                            .background(palette.content.copy(alpha = 0.12f), CircleShape)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SkipNext,
                            contentDescription = null,
                            tint = palette.content,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Skip",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.content
                        )
                    }
                }
            }

            AnimatedContent(
                targetState = state.mainMessage to state.subMessage,
                transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(250)) },
                label = "studioMessage"
            ) { (main, sub) ->
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = main,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.content,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = "  ·  $sub",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.content.copy(alpha = 0.65f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            Timeline(state.visualSchedule, blockProgress, palette.content)
        }
    }
}

/**
 * Every block in the session in order: focus blocks as bars, short breaks as dots and long
 * breaks as wider pills. Done ones are solid, the current one fills as it runs, the rest wait faintly.
 */
@Composable
private fun Timeline(items: List<SessionVisualItem>, progress: Float, ink: Color) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items.forEachIndexed { index, item ->
            val color = when (item) {
                is SessionVisualItem.FocusBlock -> scheme.primary
                is SessionVisualItem.ShortBreak -> scheme.tertiary
                is SessionVisualItem.LongBreak -> scheme.secondary
            }
            // Each block keeps its own fill, so finishing one (or skipping it) sweeps it full
            // while the next grows from empty, instead of the whole row jumping at once
            val target = when (item.status) {
                ItemStatus.Completed -> 1f
                ItemStatus.Current -> progress
                ItemStatus.Upcoming -> 0f
            }
            val fill by animateFloatAsState(
                targetValue = target,
                animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
                label = "timelineFill$index"
            )
            val sizeModifier = when (item) {
                is SessionVisualItem.FocusBlock -> Modifier.weight(1f).height(8.dp)
                is SessionVisualItem.ShortBreak -> Modifier.size(8.dp)
                is SessionVisualItem.LongBreak -> Modifier.width(20.dp).height(8.dp)
            }
            val solid = lerpTo(color, ink)
            Box(
                modifier = sizeModifier
                    .clip(CircleShape)
                    .background(ink.copy(alpha = 0.14f))
                    .drawBehind { drawFill(fill, solid) }
            )
        }
    }
}

/**
 * [fill] (0..1) of a timeline mark in [color]: bars fill from the left; marks too short to read
 * as a sweep (the break dots) grow from their centre instead.
 */
private fun DrawScope.drawFill(fill: Float, color: Color) {
    if (fill <= 0f) return
    if (size.width > size.height * 1.5f) {
        drawRoundRect(
            color = color,
            size = Size((size.width * fill).coerceAtLeast(size.height), size.height),
            cornerRadius = CornerRadius(size.height / 2f),
            alpha = (fill * 6f).coerceAtMost(1f)
        )
    } else {
        drawCircle(color = color, radius = size.minDimension / 2f * fill)
    }
}

/** The block's own colour, nudged towards the card's text colour so it always reads on the card. */
private fun lerpTo(color: Color, ink: Color): Color = androidx.compose.ui.graphics.lerp(color, ink, 0.15f)
