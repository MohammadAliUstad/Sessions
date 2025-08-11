package com.yugentech.sessions.utils

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

private const val AnimationDuration = 450
private const val EnterAnimationDuration = 550

val defaultEnterTransition: EnterTransition
    get() = slideInHorizontally(
        animationSpec = tween(
            durationMillis = EnterAnimationDuration,
            easing = FastOutSlowInEasing
        )
    ) { fullWidth -> fullWidth } + fadeIn(
        animationSpec = tween(
            durationMillis = EnterAnimationDuration,
            easing = FastOutSlowInEasing
        )
    )

val defaultExitTransition: ExitTransition
    get() = slideOutHorizontally(
        animationSpec = tween(
            durationMillis = AnimationDuration,
            easing = FastOutSlowInEasing
        )
    ) { fullWidth -> -fullWidth / 3 } + fadeOut(
        animationSpec = tween(
            durationMillis = AnimationDuration,
            easing = FastOutSlowInEasing
        )
    )

val defaultPopEnterTransition: EnterTransition
    get() = slideInHorizontally(
        animationSpec = tween(
            durationMillis = AnimationDuration,
            easing = FastOutSlowInEasing
        )
    ) { fullWidth -> -fullWidth / 3 } + fadeIn(
        animationSpec = tween(
            durationMillis = AnimationDuration,
            easing = FastOutSlowInEasing
        )
    )

val defaultPopExitTransition: ExitTransition
    get() = slideOutHorizontally(
        animationSpec = tween(
            durationMillis = AnimationDuration,
            easing = FastOutSlowInEasing
        )
    ) { fullWidth -> fullWidth } + fadeOut(
        animationSpec = tween(
            durationMillis = AnimationDuration,
            easing = FastOutSlowInEasing
        )
    )