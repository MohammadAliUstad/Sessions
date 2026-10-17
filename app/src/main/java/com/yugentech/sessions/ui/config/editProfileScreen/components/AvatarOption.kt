package com.yugentech.sessions.ui.config.editProfileScreen.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.theme.tokens.strokes

// Size of each avatar in the avatar sheet's grid, three to a row.
internal val AvatarOptionSize = 112.dp

// No outline on unselected avatars and no scale animation: only the selected one gets a thick
// primary ring.
@Composable
fun AvatarOption(
    avatar: Avatar,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
    ) {
        AvatarImage(
            avatarId = avatar.id,
            modifier = Modifier
                .clip(CircleShape)
                .clickable(onClick = onSelect)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .then(
                    if (isSelected) {
                        Modifier.border(
                            width = MaterialTheme.strokes.thick,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                    } else {
                        Modifier
                    }
                ),
            size = AvatarOptionSize,
            imageSize = 134.dp,
            imageOffsetY = 6.dp
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
                initialDelayMillis = AppAnimations.Durations.Delay,
                velocity = AppAnimations.Motion.Velocity
            )
        )
    }
}