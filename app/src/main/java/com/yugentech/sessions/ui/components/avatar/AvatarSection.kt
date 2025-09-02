package com.yugentech.sessions.ui.components.avatar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AvatarSection(
    selectedAvatarId: Int,
    onAvatarSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer, // Background role
            contentColor = MaterialTheme.colorScheme.onSurface           // Text/icon role
        ),
        shape = CardDefaults.shape // Optional, defaults to M3
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Choose Your Avatar",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface // Ensures contrast
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Pick an illustration that represents you",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Subtle text
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            AvatarGrid(
                selectedAvatarId = selectedAvatarId,
                onAvatarSelected = onAvatarSelected
            )
        }
    }
}
