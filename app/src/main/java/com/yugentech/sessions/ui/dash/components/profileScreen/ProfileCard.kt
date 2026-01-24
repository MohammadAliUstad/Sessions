package com.yugentech.sessions.ui.dash.components.profileScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.config.components.editProfileScreen.AvatarImage
import com.yugentech.sessions.ui.config.components.editProfileScreen.AvatarRepository

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileCard(
    userData: UserData,
    streakCount: Int,
    onEditProfile: () -> Unit,
    onViewInsights: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(MaterialTheme.corners.extraLarge)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.m),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
        ) {
            // Header: Name and Edit Button
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = userData.name ?: "User",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().align(Alignment.Center)
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

            // Avatar Section
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

            Text(
                text = AvatarRepository.getAvatarName(userData.avatarId) ?: "Wise Elder",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Primary Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
            ) {
                // Left: View Insights
                FilledTonalButton(
                    onClick = onViewInsights,
                    modifier = Modifier
                        .weight(1f)
                        .height(MaterialTheme.components.buttonHeight),
                    shape = RoundedCornerShape(MaterialTheme.corners.smallMedium),
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

                // Right: Streak Display (Non-clickable Button for UI consistency)
                FilledTonalButton(
                    onClick = { /* Non-interactive or triggers streak info */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(MaterialTheme.components.buttonHeight),
                    shape = RoundedCornerShape(MaterialTheme.corners.smallMedium),
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
                    Text(
                        text = "$streakCount Days",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}