package com.yugentech.sessions.ui.dash.profileScreen.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.user.model.UserData
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.config.editProfileScreen.components.AvatarPortrait
import com.yugentech.sessions.ui.config.editProfileScreen.components.AvatarRepository

// Compact profile card: avatar on the left (tap it or its badge to change it),
// name and avatar title beside it with a pencil to rename, then Insights and the streak.
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileCard(
    userData: UserData,
    streakCount: Int,
    onEditName: () -> Unit,
    onEditAvatar: () -> Unit,
    onViewInsights: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val fadeThrough = AppAnimations.Transitions.rememberExpressiveFadeThrough()
    val openAvatarSheet = {
        haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
        onEditAvatar()
    }
    // The avatar takes 120dp in the layout but is drawn at 140dp: the extra spills into the card's
    // padding, so the figure is bigger without a taller card.
    val avatarSize = 120.dp
    val avatarDrawSize = 140.dp
    // The drawings are 240x324 portraits whose figure fills only the middle ~70%, so the drawing
    // is larger than the circle to avoid empty space above and below, and nudged up.
    val avatarImageSize = 160.dp
    val avatarImageOffsetY = (-4).dp

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(MaterialTheme.corners.extraLarge)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.m),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Zoomed-in figure (no empty space above and below) over the morphing loading-indicator
                // shape; tap it or its badge to change it.
                Box(
                    modifier = Modifier
                        .size(avatarSize)
                        // Round tap ripple the size of the drawn avatar, without cropping it.
                        .clickable(
                            interactionSource = null,
                            indication = ripple(bounded = false, radius = avatarDrawSize / 2),
                            onClickLabel = "Change avatar",
                            onClick = openAvatarSheet
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator(
                        modifier = Modifier.requiredSize(avatarDrawSize),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    )
                    // A new avatar pops in (fade + scale up) while the old one fades out.
                    AnimatedContent(
                        targetState = userData.avatarId,
                        transitionSpec = {
                            if (initialState == null) {
                                // First load: show the avatar without animating from the default.
                                EnterTransition.None togetherWith ExitTransition.None
                            } else {
                                // clip = false: by default AnimatedContent crops to its bounds while
                                // animating, which showed a square edge around the scaling figure.
                                ((fadeIn(tween(220, delayMillis = 60)) +
                                        scaleIn(tween(320, delayMillis = 60), initialScale = 0.85f)) togetherWith
                                        (fadeOut(tween(150)) + scaleOut(tween(150), targetScale = 1.08f)))
                                    .using(SizeTransform(clip = false))
                            }
                        },
                        // The animated box holds the whole enlarged drawing, so no
                        // part of a tall avatar hangs outside it. Fades draw into a layer the size
                        // of this box, which used to cut the tops off until the animation ended.
                        // Nudged up a little so the head sits near the top of the circle.
                        modifier = Modifier
                            .offset(y = avatarImageOffsetY)
                            .requiredSize(avatarImageSize),
                        label = "avatar"
                    ) { avatarId ->
                        AvatarPortrait(
                            avatarId = avatarId,
                            size = avatarImageSize,
                            contentDescription = "Profile Avatar"
                        )
                    }

                    // Edit badge: a card-coloured circle (the gap that cuts into the avatar) with
                    // a smaller primary circle inside. Two plain circles rather than a fill plus
                    // a border, which let thin primary arcs show around the edge.
                    Surface(
                        onClick = openAvatarSheet,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        // Pushed out to sit on the edge of the larger drawn circle.
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 8.dp, y = 8.dp)
                            .size(36.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(3.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Change avatar",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(MaterialTheme.spacing.m))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
                ) {
                    // A renamed name fades through to the new one.
                    AnimatedContent(
                        targetState = userData.name,
                        transitionSpec = {
                            if (initialState.isNullOrEmpty()) {
                                // First load (the placeholder name is empty): no animation.
                                EnterTransition.None togetherWith ExitTransition.None
                            } else {
                                fadeThrough()
                            }
                        },
                        label = "name"
                    ) { name ->
                        Text(
                            text = name ?: "User",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    // The avatar's title rolls up to the new one when the avatar changes.
                    AnimatedContent(
                        targetState = userData.avatarId,
                        transitionSpec = {
                            if (initialState == null) {
                                EnterTransition.None togetherWith ExitTransition.None
                            } else {
                                (fadeIn(tween(220, delayMillis = 60)) +
                                        slideInVertically(tween(260, delayMillis = 60)) { it / 2 }) togetherWith
                                        (fadeOut(tween(150)) + slideOutVertically(tween(150)) { -it / 2 })
                            }
                        },
                        label = "avatarName"
                    ) { avatarId ->
                        Text(
                            text = AvatarRepository.getAvatarName(avatarId) ?: "Wise Elder",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                        onEditName()
                    },
                    modifier = Modifier.size(MaterialTheme.components.buttonSmall),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Change name",
                        modifier = Modifier.size(MaterialTheme.icons.mediumSmall)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
            ) {
                FilledTonalButton(
                    onClick = onViewInsights,
                    modifier = Modifier
                        .weight(1f)
                        .height(MaterialTheme.components.buttonHeight),
                    shape = RoundedCornerShape(MaterialTheme.corners.extraLarge),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = null,
                        modifier = Modifier.size(MaterialTheme.icons.mediumSmall)
                    )
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.s))
                    Text(
                        text = "Insights",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                FilledTonalButton(
                    onClick = { /* Non-interactive */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(MaterialTheme.components.buttonHeight),
                    shape = RoundedCornerShape(MaterialTheme.corners.extraLarge),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        modifier = Modifier.size(MaterialTheme.icons.mediumSmall)
                    )

                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.s))

                    val streakText = if (streakCount > 0) {
                        val unit = if (streakCount == 1) "Day" else "Days"
                        "$streakCount $unit"
                    } else {
                        "No Streak"
                    }

                    Text(
                        text = streakText,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
