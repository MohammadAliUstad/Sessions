package com.yugentech.sessions.utils

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

// Standard Material Design animation duration
private const val ANIMATION_DURATION = 300

// Forward navigation (entering a new screen)
val defaultEnterTransition: EnterTransition
    get() = slideInHorizontally(
        animationSpec = tween(ANIMATION_DURATION),
        initialOffsetX = { it }
    ) + fadeIn(animationSpec = tween(ANIMATION_DURATION))

val defaultExitTransition: ExitTransition
    get() = slideOutHorizontally(
        animationSpec = tween(ANIMATION_DURATION),
        targetOffsetX = { -it / 3 }
    ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))

// Back navigation (returning to previous screen)
val defaultPopEnterTransition: EnterTransition
    get() = slideInHorizontally(
        animationSpec = tween(ANIMATION_DURATION),
        initialOffsetX = { -it / 3 }
    ) + fadeIn(animationSpec = tween(ANIMATION_DURATION))

val defaultPopExitTransition: ExitTransition
    get() = slideOutHorizontally(
        animationSpec = tween(ANIMATION_DURATION),
        targetOffsetX = { it }
    ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))