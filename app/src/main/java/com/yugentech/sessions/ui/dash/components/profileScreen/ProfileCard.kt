package com.yugentech.sessions.ui.dash.components.profileScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun ProfileCard(
    userData: UserData,
    totalTime: Long,
    onEditProfile: () -> Unit
) {
    // IMPROVEMENT: Use Surface for a cleaner, flatter container that fits the "Grouped" look
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(24.dp)
    ) {
        // IMPROVEMENT: Use Box to overlay the Edit button
        // This ensures the ProfileContent remains perfectly centered in the card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.m)
        ) {
            // 1. Centered Content
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileContent(userData = userData, totalTime = totalTime)
            }

            // 2. Floating Edit Button (Top Right)
            IconButton(
                onClick = onEditProfile,
                modifier = Modifier.align(Alignment.TopEnd),
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
    }
}