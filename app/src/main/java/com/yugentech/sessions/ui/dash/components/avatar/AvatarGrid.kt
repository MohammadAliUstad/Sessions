package com.yugentech.sessions.ui.dash.components.avatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun AvatarGrid(
    selectedAvatarId: Int,
    onAvatarSelected: (Int) -> Unit
) {
    val avatars = AvatarRepository.getAllAvatars()
    val rows = avatars.chunked(3)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.l)
    ) {
        rows.forEach { rowAvatars ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.l,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowAvatars.forEach { avatar ->
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