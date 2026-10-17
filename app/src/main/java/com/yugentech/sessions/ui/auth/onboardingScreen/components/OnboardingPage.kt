package com.yugentech.sessions.ui.auth.onboardingScreen.components

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import kotlinx.coroutines.delay

@Composable
fun OnboardingPage(content: PageContent, isVisible: Boolean) {
    val spacing = MaterialTheme.spacing
    val icons = MaterialTheme.icons
    val components = MaterialTheme.components

    val (title, description, highlights, imageRes) = content

    var titleShown by remember { mutableStateOf(false) }
    var textShown by remember { mutableStateOf(false) }
    var highlightsShown by remember { mutableStateOf(false) }
    val imageState = remember { MutableTransitionState(false) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            titleShown = true
            delay(AppAnimations.Durations.Rapid.toLong())
            textShown = true
            delay(AppAnimations.Durations.Fast.toLong())
            imageState.targetState = true
            delay(AppAnimations.Durations.Base.toLong())
            highlightsShown = true
        } else {
            titleShown = false
            textShown = false
            highlightsShown = false
            imageState.targetState = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = spacing.edge),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(spacing.l))

        Reveal(visible = titleShown) {
            Text(
                text = title,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(spacing.sm))

        Reveal(visible = textShown) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(spacing.l))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visibleState = imageState,
                enter = scaleIn(
                    tween(
                        durationMillis = AppAnimations.Durations.Slow,
                        delayMillis = AppAnimations.Durations.Base
                    )
                ),
                exit = scaleOut(),
                modifier = Modifier.size(components.onboardingImageContainer)
            ) {
                WavyBackground(
                    primaryColor = MaterialTheme.colorScheme.primaryContainer,
                    secondaryColor = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.fillMaxSize()
                )
            }


            androidx.compose.animation.AnimatedVisibility(
                visibleState = imageState,
                enter = fadeIn(tween(AppAnimations.Durations.Complex)) + scaleIn(tween(AppAnimations.Durations.Complex)),
                exit = fadeOut(),
                modifier = Modifier.size(components.onboardingImageContainer)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.l))

        Reveal(visible = highlightsShown) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.s)
            ) {
                highlights.forEach { highlight ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = highlight.icon,
                            contentDescription = null,
                            modifier = Modifier.size(icons.mediumSmall),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(spacing.sm))
                        Text(
                            text = highlight.text,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.xl))
    }
}

/**
 * Fades [content] in while it rises by [AppAnimations.Durations.Micro] px, as the old
 * AnimatedVisibility did, but keeps its space in the layout even while hidden. AnimatedVisibility
 * took no space until shown, so each block popping in shrank the image's box (weight(1f)) and
 * snapped the image, part-way through its scale-in, to a new position and, on short screens, a
 * smaller size.
 */
@Composable
private fun Reveal(visible: Boolean, content: @Composable () -> Unit) {
    val progress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = if (visible) tween(AppAnimations.Durations.Complex) else tween(AppAnimations.Durations.Fast),
        label = "OnboardingReveal"
    )
    Box(
        modifier = Modifier.graphicsLayer {
            alpha = progress
            // Rises only on the way in; on the way out it just fades, as before.
            translationY = if (visible) (1f - progress) * AppAnimations.Durations.Micro else 0f
        }
    ) {
        content()
    }
}
