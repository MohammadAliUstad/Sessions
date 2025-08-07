package com.yugentech.sessions.ui.components.avatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AvatarGrid(
    selectedAvatarId: Int,
    onAvatarSelected: (Int) -> Unit
) {
    val avatars = AvatarRepository.getAllAvatars()
    val rows = avatars.chunked(3)

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        rows.forEach { rowAvatars ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowAvatars.forEach { avatar ->
                    AvatarOption(
                        avatar = avatar,
                        isSelected = selectedAvatarId == avatar.id,
                        onSelect = { onAvatarSelected(avatar.id) }
                    )
                }
                repeat(3 - rowAvatars.size) {
                    Spacer(modifier = Modifier.size(width = 90.dp, height = 110.dp))
                }
            }
        }
    }
}