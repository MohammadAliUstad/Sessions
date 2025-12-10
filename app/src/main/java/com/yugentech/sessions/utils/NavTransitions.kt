package com.yugentech.sessions.utils

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.yugentech.sessions.theme.tokens.dimensions.AppConstants

fun defaultEnterTransition(
    duration: Int = AppConstants.DEFAULT_ANIMATION_DURATION,
    initialOffsetX: Int = AppConstants.PTHOUSAND
): EnterTransition {
    return slideInHorizontally(
        animationSpec = tween(duration),
        initialOffsetX = { initialOffsetX }
    ) + fadeIn(animationSpec = tween(duration))
}

fun defaultExitTransition(
    duration: Int = AppConstants.DEFAULT_ANIMATION_DURATION,
    targetOffsetX: Int = AppConstants.MTHOUSAND
): ExitTransition {
    return slideOutHorizontally(
        animationSpec = tween(duration),
        targetOffsetX = { targetOffsetX }
    ) + fadeOut(animationSpec = tween(duration))
}

fun defaultPopEnterTransition(
    duration: Int = AppConstants.DEFAULT_ANIMATION_DURATION,
    initialOffsetX: Int = AppConstants.MTHOUSAND
): EnterTransition {
    return slideInHorizontally(
        animationSpec = tween(duration),
        initialOffsetX = { initialOffsetX }
    ) + fadeIn(animationSpec = tween(duration))
}

fun defaultPopExitTransition(
    duration: Int = AppConstants.DEFAULT_ANIMATION_DURATION,
    targetOffsetX: Int = AppConstants.PTHOUSAND
): ExitTransition {
    return slideOutHorizontally(
        animationSpec = tween(duration),
        targetOffsetX = { targetOffsetX }
    ) + fadeOut(animationSpec = tween(duration))
}