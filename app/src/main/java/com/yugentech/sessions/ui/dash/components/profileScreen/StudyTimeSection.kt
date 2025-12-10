package com.yugentech.sessions.ui.dash.components.profileScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.elevation
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun StudyTimeSection(
    formattedTime: String
) {
    val label = if (formattedTime != "0") "LIFETIME FOCUS" else "NO SESSIONS YET"

    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(MaterialTheme.corners.medium),
        tonalElevation = MaterialTheme.elevation.level2
    ) {
        Box(
            modifier = Modifier
                .padding(
                    horizontal = MaterialTheme.spacing.xl,
                    vertical = MaterialTheme.spacing.l
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    // Standard M3 Role for Eyebrows/Labels
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )

                if (formattedTime != "0") {
                    Text(
                        text = formattedTime,
                        // Standard M3 Role for Hero Stats
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}