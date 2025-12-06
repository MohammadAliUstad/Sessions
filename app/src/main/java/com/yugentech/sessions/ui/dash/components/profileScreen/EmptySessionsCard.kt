package com.yugentech.sessions.ui.dash.components.profileScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.l),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Start your first session to track your progress and build productive habits!",
                // Standard M3 Role for Empty State Body text
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.m))

        Image(
            painter = painterResource(id = R.drawable.new_beginnings),
            contentDescription = "No sessions illustration",
            modifier = Modifier.size(MaterialTheme.components.imageSizeLarge),
            contentScale = ContentScale.Fit
        )
    }
}