package com.yugentech.sessions.ui.dash.homeScreen.components.durationSelection

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.state.ItemStatus
import com.yugentech.sessions.ui.dash.state.SessionDashboardState
import com.yugentech.sessions.ui.dash.state.SessionVisualItem

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SessionProgressCard(
    state: SessionDashboardState,
    targetSets: Int,
    isTimerRunning: Boolean,
    onSkipToNext: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.components.imageSizeLarge)
            .padding(horizontal = MaterialTheme.spacing.m),
        shape = RoundedCornerShape(MaterialTheme.corners.large),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
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
                        text = if (state.isLongBreakActive) "Long Break Reached" else "Session Goal",
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
                                text = state.badgeText,
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
                                // Transition between Focus/Break modes (Fade)
                                fadeIn(tween(AppAnimations.Durations.Standard)) togetherWith
                                        fadeOut(tween(AppAnimations.Durations.Fast))
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
                                text = progress,
                                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = progress,
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
                        FilledTonalButton(
                            onClick = onSkipToNext,
                            enabled = isTimerRunning,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                    alpha = 0.15f
                                ),
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                                text = "Skip",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                AnimatedContent(
                    targetState = state.mainMessage to state.subMessage,
                    transitionSpec = {
                        fadeIn(tween(AppAnimations.Durations.Standard)) togetherWith
                                fadeOut(tween(AppAnimations.Durations.Fast))
                    },
                    label = "taglineAnimation"
                ) { (main, sub) ->
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = main,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = sub,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
            ) {
                state.visualSchedule.forEach { item ->
                    val (completedColor, activeColor, upcomingColor) = when (item) {
                        is SessionVisualItem.FocusBlock -> Triple(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onSecondaryContainer,
                            MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
                        )

                        is SessionVisualItem.ShortBreak -> Triple(
                            MaterialTheme.colorScheme.tertiary,
                            MaterialTheme.colorScheme.tertiary,
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
                        )

                        is SessionVisualItem.LongBreak -> Triple(
                            MaterialTheme.colorScheme.error,
                            MaterialTheme.colorScheme.error,
                            MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                        )
                    }

                    val color = when (item.status) {
                        ItemStatus.Completed -> completedColor
                        ItemStatus.Current -> activeColor
                        ItemStatus.Upcoming -> upcomingColor
                    }

                    when (item) {
                        is SessionVisualItem.FocusBlock -> {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(MaterialTheme.spacing.xsSmall)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        }

                        is SessionVisualItem.ShortBreak -> {
                            Box(
                                modifier = Modifier
                                    .size(MaterialTheme.spacing.xsSmall)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        }

                        is SessionVisualItem.LongBreak -> {
                            Box(
                                modifier = Modifier
                                    .width(MaterialTheme.spacing.sm)
                                    .height(MaterialTheme.spacing.sm)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        }
                    }
                }
            }
        }
    }
}