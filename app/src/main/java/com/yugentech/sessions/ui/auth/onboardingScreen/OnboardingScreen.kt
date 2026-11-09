package com.yugentech.sessions.ui.auth.onboardingScreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.ui.auth.components.AuraTheme
import com.yugentech.sessions.ui.auth.onboardingScreen.components.AmbienceScene
import com.yugentech.sessions.ui.auth.onboardingScreen.components.FocusScene
import com.yugentech.sessions.ui.auth.onboardingScreen.components.GrowthScene
import com.yugentech.sessions.ui.auth.onboardingScreen.components.SessionScene
import com.yugentech.sessions.ui.auth.onboardingScreen.components.ThemeScene
import kotlinx.coroutines.launch
import kotlin.math.abs

/** The onboarding's scenes, in order. */
private enum class Scene { Focus, Session, Ambience, Theme, Growth }

/**
 * Sessions' first run, after Quill's: five short, interactive scenes rather than a feature list.
 * A focus dial counts down at a tap, a session card fills block by block (Skip moves it on),
 * soundscapes light up an equaliser, a home card recolours to each palette, and a focus heatmap
 * fills in.
 *
 * It sits on the aura the nav host draws behind it (shared with the sign-in screens), whose glows
 * glide with each step. Story-style bars at the top fill as the user swipes; Skip leaves at any
 * point, and the button at the bottom moves on, then [finishLabel] finishes on the last scene.
 * Settings opens it again later, with "Done" in place of the first run's label.
 */
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    finishLabel: String = "Start focusing"
) {
    // The aura's colours, which the nav host draws behind this screen
    AuraTheme { OnboardingContent(onFinish, finishLabel) }
}

@Composable
private fun OnboardingContent(onFinish: () -> Unit, finishLabel: String) {
    val scenes = Scene.entries
    val pager = rememberPagerState { scenes.size }
    val scope = rememberCoroutineScope()
    // Respects the user's haptics setting (SessionsTheme gates LocalHapticFeedback).
    val haptic = LocalHapticFeedback.current
    val onLast = pager.currentPage == scenes.lastIndex
    BackHandler(enabled = pager.currentPage > 0) {
        scope.launch { pager.animateScrollToPage(pager.currentPage - 1) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Story bars and Skip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 8.dp, top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StoryBars(pager = pager, count = scenes.size, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                val skipAlpha by animateFloatAsState(if (onLast) 0f else 1f, tween(200), label = "skip")
                TextButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                        onFinish()
                    },
                    enabled = !onLast,
                    modifier = Modifier.graphicsLayer { alpha = skipAlpha }
                ) {
                    Text("Skip", style = MaterialTheme.typography.labelLarge)
                }
            }

            HorizontalPager(
                state = pager,
                modifier = Modifier.weight(1f)
            ) { page ->
                // A little depth while swiping: the scene drifts more slowly than the page and
                // eases back as it goes. It's fully faded out before the swipe ends, so a scene
                // the pager drops is never still on screen when it goes.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val offset = (pager.currentPage - page) + pager.currentPageOffsetFraction
                            val away = abs(offset).coerceIn(0f, 1f)
                            translationX = offset * size.width * 0.35f
                            alpha = (1f - away / FADE_OUT_BY).coerceIn(0f, 1f)
                            scaleX = 1f - away * 0.08f
                            scaleY = 1f - away * 0.08f
                        }
                ) {
                    val active = pager.currentPage == page
                    when (scenes[page]) {
                        Scene.Focus -> FocusScene(active)
                        Scene.Session -> SessionScene(active)
                        Scene.Ambience -> AmbienceScene(active)
                        Scene.Theme -> ThemeScene(active)
                        Scene.Growth -> GrowthScene(active)
                    }
                }
            }

            // Back (from the second scene) + the main button
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    visible = pager.currentPage > 0,
                    // clip = false: the slot widens without slicing the round button; it
                    // grows into place instead of being uncovered.
                    enter = expandHorizontally(tween(300, easing = FastOutSlowInEasing), clip = false) +
                        scaleIn(tween(300, easing = FastOutSlowInEasing), initialScale = 0.7f) +
                        fadeIn(tween(300)),
                    exit = shrinkHorizontally(tween(300, easing = FastOutSlowInEasing), clip = false) +
                        scaleOut(tween(200), targetScale = 0.7f) +
                        fadeOut(tween(150))
                ) {
                    Row {
                        BackAction(onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                            scope.launch { pager.animateScrollToPage(pager.currentPage - 1) }
                        })
                        Spacer(Modifier.width(12.dp))
                    }
                }
                Button(
                    onClick = {
                        if (onLast) {
                            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                            onFinish()
                        } else {
                            haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                            scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
                        }
                    },
                    shape = CircleShape,
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp)
                ) {
                    // Only the label swaps; the arrow stays put beside it and glides to its new
                    // place as the label's width eases to the new text's
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AnimatedContent(
                            targetState = onLast,
                            // A plain vertical swap: the new label pushes the old one out, upward on
                            // the way to the last scene and downward on the way back
                            transitionSpec = {
                                val direction = if (targetState) 1 else -1
                                (slideInVertically(tween(300)) { it * direction } + fadeIn(tween(300)))
                                    .togetherWith(slideOutVertically(tween(300)) { -it * direction } + fadeOut(tween(300)))
                                    // clip = false, as for the back button: the label area widens without
                                    // slicing the longer text; the button's own shape still bounds it
                                    .using(SizeTransform(clip = false) { _, _ -> tween(300) })
                            },
                            contentAlignment = Alignment.Center,
                            label = "onboardingButton"
                        ) { last ->
                            Text(
                                text = if (last) finishLabel else "Continue",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/** The round back button left of the main button, tinted with the theme's primary. */
@Composable
private fun BackAction(onClick: () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(primary.copy(alpha = 0.12f))
            .border(1.dp, primary.copy(alpha = 0.3f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.size(22.dp),
            tint = primary
        )
    }
}

/** How far through a swipe (0..1) a leaving scene is fully faded out. */
private const val FADE_OUT_BY = 0.6f

/**
 * One bar per scene, like stories: finished scenes full, the current one filling as the user
 * swipes toward the next, later ones empty.
 */
@Composable
private fun StoryBars(pager: PagerState, count: Int, modifier: Modifier = Modifier) {
    val track = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f)
    val fill = MaterialTheme.colorScheme.primary
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(count) { i ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(track)
                    .drawBehind {
                        val position = pager.currentPage + pager.currentPageOffsetFraction
                        val filled = (position + 1f - i).coerceIn(0f, 1f)
                        if (filled > 0f) {
                            drawRoundRect(
                                color = fill,
                                size = Size(size.width * filled, size.height),
                                cornerRadius = CornerRadius(size.height / 2f)
                            )
                        }
                    }
            )
        }
    }
}
