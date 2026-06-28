package com.yugentech.sessions.ui.auth.onboardingScreen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.auth.components.WavyBackground
import kotlinx.coroutines.delay

@Composable
fun OnboardingPage(page: Int, isVisible: Boolean) {
    val spacing = MaterialTheme.spacing
    val icons = MaterialTheme.icons
    val components = MaterialTheme.components
    val scrollState = rememberScrollState()

    val (title, description, highlights, imageRes) = when (page) {
        0 -> PageContent(
            title = "Enter Your\nOrbit",
            description = "In a noisy world, focus is your superpower. Block distractions with proven Pomodoro techniques—work in timed sessions, your way.",
            highlights = listOf(
                FeatureHighlight(Icons.Default.Settings, "Customizable sessions"),
                FeatureHighlight(Icons.Default.Timer, "Set your own focus & break times"),
                FeatureHighlight(Icons.Default.Repeat, "Multiple sets for deep work")
            ),
            imageRes = R.drawable.chaotic_good
        )

        1 -> PageContent(
            title = "Find Your\nFlow",
            description = "Create your perfect focus environment with complete control over your sessions and ambience.",
            highlights = listOf(
                FeatureHighlight(Icons.Default.MusicNote, "6 ambient sounds for focus"),
                FeatureHighlight(Icons.Default.Palette, "8 beautiful color themes"),
                FeatureHighlight(Icons.Default.DarkMode, "Light, dark & AMOLED modes")
            ),
            imageRes = R.drawable.growth
        )

        else -> PageContent(
            title = "Watch It\nGrow",
            description = "Every focused minute counts. Track your sessions, build consistency, and stay on top of your goals with smart reminders.",
            highlights = listOf(
                FeatureHighlight(Icons.Default.NotificationsActive, "Intelligent smart reminders"),
                FeatureHighlight(Icons.AutoMirrored.Filled.TrendingUp, "Track your focus progress"),
                FeatureHighlight(Icons.Default.BarChart, "Visualize your weekly rhythm")
            ),
            imageRes = R.drawable.jumping
        )
    }

    val titleState = remember { MutableTransitionState(false) }
    val textState = remember { MutableTransitionState(false) }
    val highlightsState = remember { MutableTransitionState(false) }
    val imageState = remember { MutableTransitionState(false) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            titleState.targetState = true
            delay(AppAnimations.Durations.Rapid.toLong())
            textState.targetState = true
            delay(AppAnimations.Durations.Fast.toLong())
            imageState.targetState = true
            delay(AppAnimations.Durations.Base.toLong())
            highlightsState.targetState = true
        } else {
            titleState.targetState = false
            textState.targetState = false
            highlightsState.targetState = false
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

        AnimatedVisibility(
            visibleState = titleState,
            enter = fadeIn(tween(AppAnimations.Durations.Complex)) +
                    slideInVertically(tween(AppAnimations.Durations.Complex)) { AppAnimations.Durations.Micro },
            exit = fadeOut()
        ) {
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

        AnimatedVisibility(
            visibleState = textState,
            enter = fadeIn(tween(AppAnimations.Durations.Complex)) +
                    slideInVertically(tween(AppAnimations.Durations.Complex)) { AppAnimations.Durations.Micro },
            exit = fadeOut()
        ) {
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

        AnimatedVisibility(
            visibleState = highlightsState,
            enter = fadeIn(tween(AppAnimations.Durations.Complex)) + slideInVertically(tween(AppAnimations.Durations.Complex)) { AppAnimations.Durations.Micro },
            exit = fadeOut()
        ) {
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
