package com.yugentech.sessions.ui.config.components.editProfileScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarSection(
    selectedAvatarId: Int,
    onAvatarSelected: (Int) -> Unit,
    onSaveClick: () -> Unit,
    isSaveEnabled: Boolean
) {
    // 1. Get Categories and organize data
    val categories = remember { AvatarCategory.entries }

    // Auto-select the category of the current avatar
    val initialCategory = remember(selectedAvatarId) {
        AvatarRepository.getAvatarById(selectedAvatarId)?.category ?: categories.first()
    }
    var currentCategory by remember { mutableStateOf(initialCategory) }

    // Filter avatars for the active tab
    val currentAvatars by remember(currentCategory) {
        derivedStateOf { AvatarRepository.getAvatarsByCategory(currentCategory) }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(MaterialTheme.corners.large)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Tabs ---
            Column(
                modifier = Modifier.padding(top = MaterialTheme.spacing.m),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
            ) {
                SecondaryScrollableTabRow(
                    selectedTabIndex = categories.indexOf(currentCategory),
                    edgePadding = MaterialTheme.spacing.m,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {}
                ) {
                    categories.forEach { category ->
                        Tab(
                            selected = currentCategory == category,
                            onClick = { currentCategory = category },
                            // UPDATED: This clips the ripple to a rounded shape
                            modifier = Modifier.clip(
                                RoundedCornerShape(
                                    topStart = MaterialTheme.corners.medium,
                                    topEnd = MaterialTheme.corners.medium,
                                    bottomStart = 0.dp,
                                    bottomEnd = 0.dp
                                )
                            ),
                            text = {
                                Text(
                                    text = category.displayName,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        )
                    }
                }
            }

            // --- Avatar Grid (Filtered) ---
            AnimatedContent(
                targetState = currentAvatars,
                label = "avatar_grid_transition"
            ) { avatars ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.l),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.l)
                ) {
                    avatars.chunked(3).forEach { rowAvatars ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            rowAvatars.forEach { avatar ->
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AvatarOption(
                                        avatar = avatar,
                                        isSelected = selectedAvatarId == avatar.id,
                                        onSelect = { onAvatarSelected(avatar.id) }
                                    )
                                }
                            }
                            // Fill empty space if row has < 3 items
                            repeat(3 - rowAvatars.size) {
                                Box(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // --- Save Button (Bottom Right) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = MaterialTheme.spacing.l, bottom = MaterialTheme.spacing.l),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onSaveClick,
                    enabled = isSaveEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Save")
                }
            }
        }
    }
}