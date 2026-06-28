package com.yugentech.sessions.ui.auth.onboardingScreen

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
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.auth.onboardingScreen.components.OnboardingPage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val spacing = MaterialTheme.spacing

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
                val showSkip = pagerState.currentPage < 2
                AnimatedVisibility(
                    visible = showSkip,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    TextButton(
                        onClick = onFinish,
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Text("Skip", style = MaterialTheme.typography.labelLarge)
                    }
                }

                if (!showSkip) Spacer(Modifier.width(spacing.none))

                Button(
                    onClick = {
                        scope.launch {
                            if (pagerState.currentPage < 2) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                onFinish()
                            }
                        }
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    contentPadding = PaddingValues(horizontal = spacing.l, vertical = spacing.sm)
                ) {
                    Text(
                        text = if (pagerState.currentPage < 2) "Next" else "Get Started",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(Modifier.width(spacing.s))
                    Icon(
                        imageVector = if (pagerState.currentPage < 2)
                            Icons.AutoMirrored.Filled.ArrowForward
                        else
                            Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(MaterialTheme.icons.smallMedium)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val targetProgress = (pagerState.currentPage + 1) / 3f

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
                    page = page,
                    isVisible = (pagerState.currentPage == page)
                )
            }
        }
    }
}
