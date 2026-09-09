package com.yugentech.sessions.ui.dash.mainScreen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.utils.AppConstants
import com.yugentech.sessions.theme.tokens.spacing
import kotlinx.coroutines.delay

@Composable
fun ToastMessage(
    message: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    duration: Long = 3000L
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(message) {
        if (message != null) {
            isVisible = true
            delay(duration)
            isVisible = false
            delay(300)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible && message != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.m),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(MaterialTheme.corners.pill),
                color = MaterialTheme.colorScheme.inverseSurface,
                shadowElevation = 6.dp
            ) {
                Text(
                    text = message ?: AppConstants.EMPTY,
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.spacing.l,
                        vertical = MaterialTheme.spacing.s
                    ),
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}