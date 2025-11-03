package com.yugentech.sessions.ui.dash.components.avatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun AvatarGrid(
    selectedAvatarId: Int,
    onAvatarSelected: (Int) -> Unit
) {
    val avatars = AvatarRepository.getAllAvatars()
    val rows = avatars.chunked(3)

    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.l)
    ) {
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
                if (rowAvatars.size < 3) {
                    repeat(3 - rowAvatars.size) {
                        Box(
                            modifier = Modifier.weight(1f, fill = false),
                            contentAlignment = Alignment.Center
                        ) {
                            Spacer(
                                modifier = Modifier.size(
                                    width = MaterialTheme.components.cardMinWidth,
                                    height = MaterialTheme.components.imageSizeMedium
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}