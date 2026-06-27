package com.yugentech.sessions.ui.config.aboutScreen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AppInfoCard() {
    var isAnimating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
                .padding(
                    vertical = MaterialTheme.spacing.xl,
                    horizontal = MaterialTheme.spacing.l
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .size(MaterialTheme.components.imageSizeMedium)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (!isAnimating) {
                            isAnimating = true
                            scope.launch {
                                delay(900) // match AVD duration
                                isAnimating = false
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                AnimatedSessionsIcon(
                    isAnimating = isAnimating,
                    modifier = Modifier.requiredSize(MaterialTheme.components.imageSizeLarge * 1.4f)
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.l))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))

            Text(
                text = stringResource(R.string.version),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

            Text(
                text = stringResource(R.string.app_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.s)
            )
        }
    }
}