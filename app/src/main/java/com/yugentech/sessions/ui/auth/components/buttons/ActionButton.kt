package com.yugentech.sessions.ui.auth.components.buttons

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.strokes

@Composable
fun ActionButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.components.buttonMedium),
        shape = RoundedCornerShape(MaterialTheme.corners.extraLarge),
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = {
                fadeIn(
                    tween(AppAnimations.Durations.Standard)
                ) togetherWith fadeOut(
                    tween(AppAnimations.Durations.Standard)
                )
            },
            label = "loading"
        ) { loading ->
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(MaterialTheme.icons.mediumSmall),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = MaterialTheme.strokes.medium
                )
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}