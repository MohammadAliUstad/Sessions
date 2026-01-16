package com.yugentech.sessions.ui.dash.components.profileScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun ProfileSectionHeader(
    title: String,
    icon: ImageVector? = null,
    countLabel: String? = null,
    // Updated: Default to a nice accent color (Tertiary Container)
    containerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    // Updated: Default text/icon color to match the container
    contentColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    shape: Shape = RoundedCornerShape(16.dp),
    modifier: Modifier = Modifier
) {
    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = shape,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spacing.m,
                    vertical = 12.dp // Consistent padding for the "Card" look
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Group Icon + Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        // No hardcoded tint; uses 'contentColor' (onTertiaryContainer)
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    // No hardcoded color; uses 'contentColor'
                )
            }

            // Count Label
            if (countLabel != null) {
                Text(
                    text = countLabel,
                    style = MaterialTheme.typography.labelMedium,
                    // Use a slightly transparent version of the content color for hierarchy
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}