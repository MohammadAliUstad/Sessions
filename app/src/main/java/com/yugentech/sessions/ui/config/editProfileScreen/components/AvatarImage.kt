package com.yugentech.sessions.ui.config.editProfileScreen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.components

@Composable
fun AvatarImage(
    avatarId: Int?,
    modifier: Modifier = Modifier,
    size: Dp = MaterialTheme.components.imageSizeMedium,
    // The drawing's own size, which can be larger than the circle (`size`) so the figure fills
    // it; the drawings are tall portraits with empty space around the figure. Positive
    // imageOffsetY moves the figure down inside the circle.
    imageSize: Dp = size,
    imageOffsetY: Dp = 0.dp,
    contentDescription: String? = null
) {
    val avatar = AvatarRepository.getAvatarById(avatarId) ?: AvatarRepository.getDefaultAvatar()

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = avatar.drawableRes),
            contentDescription = contentDescription ?: avatar.name,
            modifier = Modifier
                .requiredSize(imageSize)
                .offset(y = imageOffsetY)
        )
    }
}

// The whole avatar drawing, uncropped, at `size` (the caller zooms by passing a size larger than
// the space it sits in). Nothing draws outside its own bounds, so a fade or scale animation
// around it never cuts part of the figure off.
@Composable
fun AvatarPortrait(
    avatarId: Int?,
    size: Dp,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val avatar = AvatarRepository.getAvatarById(avatarId) ?: AvatarRepository.getDefaultAvatar()

    Image(
        painter = painterResource(id = avatar.drawableRes),
        contentDescription = contentDescription ?: avatar.name,
        modifier = modifier.size(size)
    )
}