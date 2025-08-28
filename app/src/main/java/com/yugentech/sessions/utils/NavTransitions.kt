package com.yugentech.sessions.utils

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

// Adjusted durations for smoother slide animations
private const val AnimationDuration = 400
private const val EnterAnimationDuration = 500

val defaultEnterTransition: EnterTransition
    get() = slideInHorizontally(
        animationSpec = tween(
            durationMillis = EnterAnimationDuration,
            easing = FastOutSlowInEasing
        )
    ) { fullWidth -> fullWidth }

val defaultExitTransition: ExitTransition
    get() = slideOutHorizontally(
        animationSpec = tween(
            durationMillis = AnimationDuration,
            easing = FastOutSlowInEasing
        )
    ) { fullWidth -> -fullWidth / 3 }

val defaultPopEnterTransition: EnterTransition
    get() = slideInHorizontally(
        animationSpec = tween(
            durationMillis = AnimationDuration,
            easing = FastOutSlowInEasing
        )
    ) { fullWidth -> -fullWidth / 3 }

val defaultPopExitTransition: ExitTransition
    get() = slideOutHorizontally(
        animationSpec = tween(
            durationMillis = AnimationDuration,
            easing = FastOutSlowInEasing
        )
    ) { fullWidth -> fullWidth }