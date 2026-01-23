package com.yugentech.sessions.ui.config.components.editProfileScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarSection(
    selectedAvatarId: Int,
    onAvatarSelected: (Int) -> Unit
) {
    val categories = remember { AvatarCategory.entries }

    val initialCategory = remember(selectedAvatarId) {
        AvatarRepository.getAvatarById(selectedAvatarId)?.category ?: categories.first()
    }

    var currentCategory by remember { mutableStateOf(initialCategory) }

    val currentAvatars by remember(currentCategory) {
        derivedStateOf { AvatarRepository.getAvatarsByCategory(currentCategory) }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(MaterialTheme.corners.extraLarge)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                            modifier = Modifier.clip(
                                RoundedCornerShape(
                                    topStart = MaterialTheme.corners.medium,
                                    topEnd = MaterialTheme.corners.medium
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

            AnimatedContent(
                targetState = currentAvatars,
                label = "avatar_grid"
            ) { avatars ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.l),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
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
                        }
                    }
                }
            }
        }
    }
}