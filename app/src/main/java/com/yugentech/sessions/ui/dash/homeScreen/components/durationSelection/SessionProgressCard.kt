package com.yugentech.sessions.ui.dash.homeScreen.components.durationSelection

import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import com.yugentech.sessions.localization.asString
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.yugentech.sessions.theme.config.CardAnimationStyle
import com.yugentech.sessions.theme.config.CardStyle
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.HeroCardBackground
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.SessionPulse
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.heroBrush
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.heroPalette
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.heroSurface
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.rememberEffectTime
import com.yugentech.sessions.ui.dash.state.SessionDashboardState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SessionProgressCard(
    state: SessionDashboardState,
    targetSets: Int,
    isTimerRunning: Boolean,
    onSkipToNext: () -> Unit,
    cardStyle: CardStyle = CardStyle.FILLED,
    animationStyle: CardAnimationStyle = CardAnimationStyle.NONE,
    pulse: SessionPulse = SessionPulse(),
    // How far the current block has run (0..1); fills the current focus bar
    blockProgress: Float = pulse.progress,
    // Home's own side margin; the Appearance preview sits in a list that has its own
    horizontalPadding: Dp = MaterialTheme.spacing.m
) {
    val fadeThrough = AppAnimations.Transitions.rememberExpressiveFadeThrough()
    val palette = heroPalette(cardStyle)
    val scheme = MaterialTheme.colorScheme
    // The effect's own time, which only moves while the timer runs
    val time = if (animationStyle != CardAnimationStyle.NONE) rememberEffectTime(pulse.running) else null

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.components.imageSizeLarge)
            .padding(horizontal = horizontalPadding)
            .heroSurface(cardStyle, heroBrush(), RoundedCornerShape(MaterialTheme.corners.large))
    ) {
        HeroCardBackground(
            style = animationStyle,
            time = time,
            pulse = pulse,
            ink = palette.ink,
            shade = palette.shade,
            modifier = Modifier.matchParentSize()
        )

        // Every piece of the card reads onSecondaryContainer, so swapping it here recolours them all.
        MaterialTheme(colorScheme = scheme.copy(onSecondaryContainer = palette.content)) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(MaterialTheme.spacing.m),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Flag,
                            contentDescription = null,
                            modifier = Modifier.size(MaterialTheme.icons.medium),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.xsSmall))

                        Text(
                            text = if (state.isLongBreakActive) stringResource(R.string.card_long_break_reached) else stringResource(R.string.card_session_goal),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f)
                        )
                    }

                    if (state.showLongBreakBadge) {
                        Surface(
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(MaterialTheme.corners.smallMedium)
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = MaterialTheme.spacing.sm,
                                    vertical = MaterialTheme.spacing.xs
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Timer,
                                    contentDescription = null,
                                    modifier = Modifier.size(MaterialTheme.icons.small),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                )

                                Spacer(modifier = Modifier.width(MaterialTheme.spacing.xxs))

                                Text(
                                    text = state.badgeText.asString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                                    maxLines = 1
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
                    ) {
                        AnimatedContent(
                            targetState = state.isLongBreakActive to state.progressDisplay,
                            transitionSpec = {
                                if (targetState.first != initialState.first) {
                                    // Transition between Focus/Break modes (fade through)
                                    fadeThrough()
                                } else {
                                    // Transition between numbers (Vertical Slide)
                                    (slideInVertically { it } + fadeIn(tween(AppAnimations.Durations.Standard)))
                                        .togetherWith(slideOutVertically { -it } + fadeOut(tween(AppAnimations.Durations.Fast)))
                                }
                            },
                            label = "progressAnimation"
                        ) { (isLongBreak, progress) ->
                            if (isLongBreak) {
                                Text(
                                    text = progress.asString(),
                                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = progress.asString(),
                                        style = MaterialTheme.typography.displaySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = "/$targetSets",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                            alpha = 0.5f
                                        ),
                                        modifier = Modifier.padding(start = MaterialTheme.spacing.xxs)
                                    )
                                }
                            }
                        }

                        if (targetSets > 1) {
                            // Eases between its running and paused colours, like the sound badge,
                            // instead of snapping when the session is paused or resumed. The running
                            // tint is translucent, so the container blends premultiplied: a plain colour
                            // animation raises the alpha before the hue moves and flashes the light tint.
                            val pausedFraction by animateFloatAsState(
                                targetValue = if (isTimerRunning) 0f else 1f,
                                animationSpec = spring(),
                                label = "skipContainer"
                            )
                            val skipContainer = premultipliedLerp(
                                MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.15f),
                                MaterialTheme.colorScheme.surfaceContainerHigh,
                                pausedFraction
                            )
                            val skipContent by animateColorAsState(
                                targetValue = if (isTimerRunning)
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                label = "skipContent"
                            )
                            FilledTonalButton(
                                onClick = onSkipToNext,
                                enabled = isTimerRunning,
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = skipContainer,
                                    contentColor = skipContent,
                                    disabledContainerColor = skipContainer,
                                    disabledContentColor = skipContent
                                ),
                                contentPadding = PaddingValues(
                                    horizontal = MaterialTheme.spacing.sm,
                                    vertical = MaterialTheme.spacing.xs
                                ),
                                modifier = Modifier.height(MaterialTheme.components.buttonSmall)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = null,
                                    modifier = Modifier.size(MaterialTheme.icons.smallMedium)
                                )
                                Spacer(modifier = Modifier.width(MaterialTheme.spacing.xxs))
                                Text(
                                    text = stringResource(R.string.skip),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    AnimatedContent(
                        targetState = state.mainMessage to state.subMessage,
                        transitionSpec = { fadeThrough() },
                        label = "taglineAnimation"
                    ) { (main, sub) ->
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = main.asString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = sub.asString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }

                SessionTimeline(
                    items = state.visualSchedule,
                    progress = blockProgress,
                    ink = palette.content
                )
            }
        }
    }
}

/**
 * Blends [from] to [to] by [fraction] in premultiplied alpha, so a translucent colour fading into an
 * opaque one cross-fades evenly instead of flashing its own hue at full strength midway.
 */
private fun premultipliedLerp(from: Color, to: Color, fraction: Float): Color {
    val t = fraction.coerceIn(0f, 1f)
    val alpha = from.alpha + (to.alpha - from.alpha) * t
    if (alpha <= 0f) return Color.Transparent
    fun channel(a: Float, b: Float) =
        ((a * from.alpha) + ((b * to.alpha) - (a * from.alpha)) * t) / alpha
    return Color(
        red = channel(from.red, to.red).coerceIn(0f, 1f),
        green = channel(from.green, to.green).coerceIn(0f, 1f),
        blue = channel(from.blue, to.blue).coerceIn(0f, 1f),
        alpha = alpha
    )
}
