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
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.models.UserData
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
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.m),
            horizontalAlignment = Alignment.CenterHorizontally,
            // UPDATED: This enforces equal constant vertical spacing between all 4 items
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
        ) {
            // 1. Header: Name (Centered) + Edit Button (Right)
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = userData.name ?: "User",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().align(Alignment.Center)
                )

                IconButton(
                    onClick = onEditProfile,
                    modifier = Modifier.size(48.dp).align(Alignment.CenterEnd),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile"
                    )
                }
            }

            // 2. Avatar "Hero" Section
            // Note: The Box size defines the boundaries for spacing.
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(150.dp)
            ) {
                LoadingIndicator(
                    modifier = Modifier.size(150.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                )

                AvatarImage(
                    avatarId = userData.avatarId,
                    size = 250.dp,
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
            // (No manual Spacer needed here; Arrangement.spacedBy handles it)
            StudyTimeSection(
                formattedTime = formatTime(totalTime)
            )
        }
    }
}