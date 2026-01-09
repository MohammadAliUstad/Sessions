package com.yugentech.sessions.ui.dash.components.profileScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun ProfileSectionHeader(
    title: String,
    icon: ImageVector? = null,
    countLabel: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = MaterialTheme.spacing.m,
                end = MaterialTheme.spacing.m, // Kept end padding for the countLabel
                top = MaterialTheme.spacing.m,     // Matched Settings padding
                bottom = MaterialTheme.spacing.s   // Matched Settings padding (was xs)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Group Icon + Title together
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary, // Matched Settings Color
                    modifier = Modifier.size(20.dp)
                )

                // Matched Settings Spacer
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
            }

            Text(
                text = title,
                // Matched Settings Typography & Font Weight
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                // Matched Settings Color
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Count Label (Unique to ProfileSection)
        if (countLabel != null) {
            Text(
                text = countLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}