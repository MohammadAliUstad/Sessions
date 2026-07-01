package com.yugentech.sessions.ui.dash.mainScreen.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun SectionHeader(
    icon: ImageVector,
    title: String,
    trailingText: String? = null,
    onDelete: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = MaterialTheme.spacing.s,
                top = MaterialTheme.spacing.m
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(MaterialTheme.icons.mediumSmall)
        )

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))

        AnimatedContent(
            targetState = title,
            transitionSpec = {
                if (targetState != initialState) {
                    (slideInVertically { height -> height } + fadeIn() togetherWith
                            slideOutVertically { height -> -height } + fadeOut())
                        .using(SizeTransform(clip = false))
                } else {
                    fadeIn() togetherWith fadeOut()
                }
            },
            label = "title_animation",
            modifier = Modifier.weight(1f)
        ) { targetTitle ->
            Text(
                text = targetTitle,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
        }

        if (trailingText != null) {
            AnimatedContent(
                targetState = trailingText,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "trailing_text_animation"
            ) { targetTrailing ->
                Text(
                    text = targetTrailing,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        if (onDelete != null) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(MaterialTheme.icons.medium)
            ) {
                Icon(
                    imageVector = Icons.Rounded.DeleteForever,
                    contentDescription = "Delete Group",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(MaterialTheme.icons.smallMedium)
                )
            }
        }
    }
}