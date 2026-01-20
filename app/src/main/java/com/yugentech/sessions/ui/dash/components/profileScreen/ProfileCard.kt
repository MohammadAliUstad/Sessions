package com.yugentech.sessions.ui.dash.components.profileScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.components.editProfileScreen.AvatarImage
import com.yugentech.sessions.ui.dash.components.editProfileScreen.AvatarRepository
import com.yugentech.sessions.utils.formatTime

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileCard(
    userData: UserData,
    totalTime: Long,
    onEditProfile: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(MaterialTheme.corners.medium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.m),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
        ) {
            // 1. Header:  Name (Centered) + Edit Button (Right)
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = userData.name ?: "User",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(end = MaterialTheme.components.buttonMedium)
                )

                IconButton(
                    onClick = onEditProfile,
                    modifier = Modifier
                        .size(MaterialTheme.components.buttonMedium)
                        .align(Alignment.CenterEnd),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        modifier = Modifier.size(MaterialTheme.icons.mediumSmall)
                    )
                }
            }

            // 2. Avatar "Hero" Section
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(MaterialTheme.components.imageSizeLarge)
            ) {
                LoadingIndicator(
                    modifier = Modifier.size(MaterialTheme.components.imageSizeLarge),
                    color = MaterialTheme.colorScheme.secondaryContainer
                )

                AvatarImage(
                    avatarId = userData.avatarId,
                    size = MaterialTheme.components.imageSizeLarge,
                    contentDescription = "Profile Avatar"
                )
            }

            // 3. Avatar Title / Role
            Text(
                text = AvatarRepository.getAvatarName(userData.avatarId) ?: "Wise Elder",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // 4. Lifetime Stats
            StudyTimeSection(
                formattedTime = formatTime(totalTime)
            )
        }
    }
}