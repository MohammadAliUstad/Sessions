package com.yugentech.sessions.ui.dash.components.profileScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun EmptySessionsCard(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            // Use token spacing instead of hardcoded values
            .padding(
                vertical = MaterialTheme.spacing.m,
                horizontal = MaterialTheme.spacing.m
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Use token for image size - responsive and consistent
        Image(
            painter = painterResource(id = R.drawable.new_beginnings),
            contentDescription = "No sessions illustration",
            modifier = Modifier.size(MaterialTheme.components.imageSizeLarge),
            contentScale = ContentScale.Fit
        )

        // Use token spacing between elements
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

        // Use fillMaxWidth with padding instead of fillMaxWidth(0.85f)
        Text(
            text = "Start your first session to track your progress and build productive habits! ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.m)
        )
    }
}