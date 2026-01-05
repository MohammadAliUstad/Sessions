package com.yugentech.sessions.ui.dash.components.profileScreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.components.avatar.AvatarImage
import com.yugentech.sessions.ui.dash.components.avatar.AvatarRepository
import com.yugentech.sessions.utils.formatTime

@Composable
fun ProfileContent(
    userData: UserData,
    totalTime: Long
) {
    // 1. User Name
    Text(
        text = userData.name ?: "User",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth() // Ensures centering works even for long names
    )

    Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

    // 2. Avatar Image
    AvatarImage(
        avatarId = userData.avatarId,
        size = MaterialTheme.components.imageSizeLarge,
        contentDescription = "Profile Avatar"
    )

    Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))

    // 3. Avatar Title / Role
    Text(
        text = AvatarRepository.getAvatarName(userData.avatarId) ?: "Wise Elder",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(MaterialTheme.spacing.l))

    // 4. Lifetime Stats
    StudyTimeSection(
        formattedTime = formatTime(totalTime)
    )
}