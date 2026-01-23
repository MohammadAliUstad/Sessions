package com.yugentech.sessions.ui.config.components.editProfileScreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.theme.tokens.strokes

@Composable
fun AvatarOption(
    avatar: Avatar,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(AppAnimations.Durations.Standard),
        label = "avatar_scale"
    )

    Column(
        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
    ) {
        AvatarImage(
            avatarId = avatar.id,
            modifier = Modifier
                .scale(scale)
                .clip(CircleShape)
                .clickable(onClick = onSelect)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .border(
                    width = if (isSelected)
                        MaterialTheme.strokes.medium
                    else
                        MaterialTheme.strokes.thin,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    },
                    shape = CircleShape
                ),
            size = MaterialTheme.components.imageSizeMedium
        )

        Text(
            text = avatar.name,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.basicMarquee(
                animationMode = MarqueeAnimationMode.Immediately,
                repeatDelayMillis = AppAnimations.Durations.RepeatDelay,
                initialDelayMillis = AppAnimations.Durations.InitialDelay,
                velocity = AppAnimations.Motion.Velocity
            )
        )
    }
}