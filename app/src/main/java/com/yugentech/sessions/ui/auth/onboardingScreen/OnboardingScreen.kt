package com.yugentech.sessions.ui.auth.onboardingScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.auth.onboardingScreen.components.OnboardingPage
import com.yugentech.sessions.ui.auth.onboardingScreen.components.onboardingPages
import kotlinx.coroutines.launch

// Quill's onboarding, line for line, with Sessions' pages (components/OnboardingPages.kt).
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    // Respects the user's haptics setting (SessionsTheme gates LocalHapticFeedback).
    val haptic = LocalHapticFeedback.current
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val lastPage = onboardingPages.lastIndex
    val scope = rememberCoroutineScope()
    val spacing = MaterialTheme.spacing
    val fadeThrough = AppAnimations.Transitions.rememberExpressiveFadeThrough()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.l, vertical = spacing.xl),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val showSkip = pagerState.currentPage < lastPage
                AnimatedVisibility(
                    visible = showSkip,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    TextButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                            onFinish()
                        },
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Text("Skip", style = MaterialTheme.typography.labelLarge)
                    }
                }

                if (!showSkip) Spacer(Modifier.width(spacing.none))

                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                        scope.launch {
                            if (pagerState.currentPage < lastPage) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                onFinish()
                            }
                        }
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    contentPadding = PaddingValues(horizontal = spacing.l, vertical = spacing.sm)
                ) {
                    // Fades through between "Next" and "Get Started"; AnimatedContent's default
                    // size transform also smoothly resizes the button to fit the longer label.
                    AnimatedContent(
                        targetState = pagerState.currentPage == lastPage,
                        transitionSpec = { fadeThrough() },
                        label = "OnboardingButtonLabel"
                    ) { isLastPage ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isLastPage) "Get Started" else "Next",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(Modifier.width(spacing.s))
                            Icon(
                                imageVector = if (isLastPage)
                                    Icons.Default.Check
                                else
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(MaterialTheme.icons.smallMedium)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val targetProgress = (pagerState.currentPage + 1) / onboardingPages.size.toFloat()

            val animatedProgress by animateFloatAsState(
                targetValue = targetProgress,
                animationSpec = tween(
                    durationMillis = AppAnimations.Durations.Delay,
                    easing = AppAnimations.Easings.Standard
                ),
                label = "OnboardingProgress"
            )

            LinearWavyProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.m, bottom = spacing.s)
                    .height(MaterialTheme.components.onboardingIndicatorHeight),
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPage(
                    content = onboardingPages[page],
                    isVisible = (pagerState.currentPage == page)
                )
            }
        }
    }
}
