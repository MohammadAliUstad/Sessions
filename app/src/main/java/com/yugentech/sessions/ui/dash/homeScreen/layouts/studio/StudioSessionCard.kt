package com.yugentech.sessions.ui.dash.homeScreen.layouts.studio

import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import com.yugentech.sessions.localization.asString
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yugentech.sessions.theme.config.CardAnimationStyle
import com.yugentech.sessions.theme.config.CardStyle
import com.yugentech.sessions.ui.dash.homeScreen.components.durationSelection.SessionTimeline
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.HeroCardBackground
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.SessionPulse
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.heroBrush
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.heroPalette
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.heroSurface
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.rememberEffectTime
import com.yugentech.sessions.ui.dash.mainScreen.components.pressClickable
import com.yugentech.sessions.ui.dash.state.SessionDashboardState

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
                    text = if (state.isLongBreakActive) {
                        stringResource(R.string.mode_long_break).uppercase()
                    } else {
                        stringResource(R.string.card_set_of, state.progressDisplay.asString(), targetSets).uppercase()
                    },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.8.sp,
                    color = palette.content.copy(alpha = 0.75f),
                    modifier = Modifier.weight(1f)
                )
                // Stays in place while paused, just dimmed and inactive, as in the other layouts
                val skipAlpha by animateFloatAsState(
                    targetValue = if (isRunning) 1f else 0.4f,
                    animationSpec = tween(250, easing = FastOutSlowInEasing),
                    label = "studioSkipAlpha"
                )
                AnimatedVisibility(visible = targetSets > 1, enter = fadeIn(), exit = fadeOut()) {
                    Row(
                        modifier = Modifier
                            .pressClickable(
                                shape = CircleShape,
                                enabled = isRunning,
                                onClickLabel = stringResource(R.string.cd_skip_to_next),
                                onClick = onSkip
                            )
                            .graphicsLayer { alpha = skipAlpha }
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
                            text = stringResource(R.string.skip),
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
                        text = main.asString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.content,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = "  ·  ${sub.asString()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.content.copy(alpha = 0.65f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            SessionTimeline(state.visualSchedule, blockProgress, palette.content)
        }
    }
}
