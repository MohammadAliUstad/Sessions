package com.yugentech.sessions.ui.dash.components.avatar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AvatarImage(
    avatarId: Int?,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    contentDescription: String? = null
) {
    val avatar = AvatarRepository.getAvatarById(avatarId) ?: AvatarRepository.getDefaultAvatar()

    Surface(
        modifier = modifier.size(size).clip(CircleShape),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = avatar.drawableRes),
                contentDescription = contentDescription ?: avatar.name,
                modifier = Modifier.size(size * 0.7f)
            )
        }
    }
}