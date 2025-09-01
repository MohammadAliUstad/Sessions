package com.yugentech.sessions.ui.config.components.editProfileScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.yugentech.sessions.theme.tokens.components

@Composable
fun AvatarImage(
    avatarId: Int?,
    modifier: Modifier = Modifier,
    size: Dp = MaterialTheme.components.imageSizeMedium,
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
            modifier = Modifier.size(size)
        )
    }
}