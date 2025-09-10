package com.yugentech.sessions.utils

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.yugentech.sessions.utils.Constants.DEFAULT_ANIMATION_DURATION

fun defaultEnterTransition(
    duration: Int = DEFAULT_ANIMATION_DURATION,
    initialOffsetX: Int = 1000
): EnterTransition {
    return slideInHorizontally(
        animationSpec = tween(duration),
        initialOffsetX = { initialOffsetX }
    ) + fadeIn(animationSpec = tween(duration))
}

fun defaultExitTransition(
    duration: Int = DEFAULT_ANIMATION_DURATION,
    targetOffsetX: Int = -1000
): ExitTransition {
    return slideOutHorizontally(
        animationSpec = tween(duration),
        targetOffsetX = { targetOffsetX }
    ) + fadeOut(animationSpec = tween(duration))
}

fun defaultPopEnterTransition(
    duration: Int = DEFAULT_ANIMATION_DURATION,
    initialOffsetX: Int = -1000
): EnterTransition {
    return slideInHorizontally(
        animationSpec = tween(duration),
        initialOffsetX = { initialOffsetX }
    ) + fadeIn(animationSpec = tween(duration))
}

fun defaultPopExitTransition(
    duration: Int = DEFAULT_ANIMATION_DURATION,
    targetOffsetX: Int = 1000
): ExitTransition {
    return slideOutHorizontally(
        animationSpec = tween(duration),
        targetOffsetX = { targetOffsetX }
    ) + fadeOut(animationSpec = tween(duration))
}