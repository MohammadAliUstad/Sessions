package com.yugentech.sessions.ui.config.components.aboutScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun AboutContactCard(
    onEmailClick: () -> Unit,
    onGitHubClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(MaterialTheme.corners.medium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.m) // Reduced outer padding for list look
        ) {
            AboutActionItem(
                icon = Icons.Default.Email,
                title = stringResource(R.string.contact_developer),
                subtitle = stringResource(R.string.get_in_touch_for_support_or_feedback),
                onClick = onEmailClick
            )

            // Optional: Divider between items for cleaner separation
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.s),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            AboutActionItem(
                icon = Icons.Default.Info,
                title = stringResource(R.string.visit_github),
                subtitle = stringResource(R.string.view_source_code_and_contribute),
                onClick = onGitHubClick
            )
        }
    }
}