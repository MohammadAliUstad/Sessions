package com.yugentech.sessions.ui.dash.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yugentech.sessions.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveOnboardingScreen(
    onFinish: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        bottomBar = {
            // Standard M3 Expressive Navigation Layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // SKIP BUTTON (Left)
                // Only visible on first two pages
                val showSkip = pagerState.currentPage < 2
                AnimatedVisibility(
                    visible = showSkip,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    TextButton(
                        onClick = onFinish,
                        shape = MaterialTheme.shapes.extraLarge // Expressive Shape
                    ) {
                        Text("Skip", style = MaterialTheme.typography.labelLarge)
                    }
                }

                if (!showSkip) Spacer(Modifier.width(1.dp)) // Spacer to keep alignment if needed

                // NEXT / FINISH BUTTON (Right)
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
                    shape = MaterialTheme.shapes.extraLarge, // Standard M3 Expressive Pill shape
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage < 2) "Next" else "Get Started",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = if (pagerState.currentPage < 2)
                            Icons.AutoMirrored.Filled.ArrowForward
                        else
                            Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
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

            // 2. Animate to that target (Smoothly)
            val animatedProgress by animateFloatAsState(
                targetValue = targetProgress,
                animationSpec = tween(
                    durationMillis = 800, // Slightly longer for a "relaxed" feel
                    easing = FastOutSlowInEasing
                ),
                label = "OnboardingProgress"
            )

            // 3. Pass the ANIMATED value to the indicator
            LinearWavyProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
                    .height(10.dp),
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

@Composable
fun OnboardingPage(page: Int, isVisible: Boolean) {
    // Define Content Data
    val (title, description, highlights, imageRes) = when (page) {
        0 -> PageContent(
            title = "Enter Your\nOrbit",
            description = "In a noisy world, focus is your superpower. Block distractions with proven Pomodoro techniques—work in timed sessions, your way.",
            highlights = listOf(
                FeatureHighlight(Icons.Default.Settings, "Customizable focus sessions"),
                FeatureHighlight(Icons.Default.Timer, "Set your own work & break times"),
                FeatureHighlight(Icons.Default.Repeat, "Multiple sets for deep work")
            ),
            imageRes = R.drawable.chaotic_good
        )
        1 -> PageContent(
            title = "Find Your\nFlow",
            description = "Create your perfect focus environment with complete control over your sessions and ambience.",
            highlights = listOf(
                FeatureHighlight(Icons.Default.MusicNote, "5 ambient sounds for focus"),
                FeatureHighlight(Icons.Default.Palette, "8 beautiful color themes"),
                FeatureHighlight(Icons.Default.DarkMode, "Light, dark & AMOLED modes")
            ),
            imageRes = R.drawable.growth
        )
        else -> PageContent(
            title = "Watch It\nGrow",
            description = "Every focused minute counts. Track your sessions, build consistency, and watch your focus habits flourish over time.",
            highlights = listOf(
                FeatureHighlight(Icons.Default.CalendarToday, "Detailed session history"),
                FeatureHighlight(Icons.AutoMirrored.Filled.TrendingUp, "Track your focus streaks"),
                FeatureHighlight(Icons.Default.BarChart, "See your progress daily")
            ),
            imageRes = R.drawable.jumping
        )
    }

    // Animation States
    val titleState = remember { MutableTransitionState(false) }
    val textState = remember { MutableTransitionState(false) }
    val highlightsState = remember { MutableTransitionState(false) }
    val imageState = remember { MutableTransitionState(false) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            titleState.targetState = true
            delay(100)
            textState.targetState = true
            delay(150)
            imageState.targetState = true
            delay(200)
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
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Animated Title
        androidx.compose.animation.AnimatedVisibility(
            visibleState = titleState,
            enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { 50 },
            exit = fadeOut()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 44.sp,
                    letterSpacing = (-1).sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Animated Description
        androidx.compose.animation.AnimatedVisibility(
            visibleState = textState,
            enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { 50 },
            exit = fadeOut()
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start
            )
        }

        // --- GAP INCREASED HERE ---
        Spacer(modifier = Modifier.height(56.dp))

        // The Illustration - Smaller & Centered
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background Layer (The Wavy Indicator)
            androidx.compose.animation.AnimatedVisibility(
                visibleState = imageState,
                enter = scaleIn(tween(700, delayMillis = 200)),
                exit = scaleOut()
            ) {
                WavyBackground(
                    primaryColor = MaterialTheme.colorScheme.primaryContainer,
                    secondaryColor = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.size(240.dp)
                )
            }

            // Foreground Layer (The Illustration)
            androidx.compose.animation.AnimatedVisibility(
                visibleState = imageState,
                enter = fadeIn(tween(600)) + scaleIn(tween(600)),
                exit = fadeOut()
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // --- GAP INCREASED HERE ---
        Spacer(modifier = Modifier.height(48.dp))

        // Feature Highlights
        androidx.compose.animation.AnimatedVisibility(
            visibleState = highlightsState,
            enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { 30 },
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = highlight.text,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 22.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

// Helper data classes
private data class FeatureHighlight(
    val icon: ImageVector,
    val text: String
)

private data class PageContent(
    val title: String,
    val description: String,
    val highlights: List<FeatureHighlight>,
    val imageRes: Int
)