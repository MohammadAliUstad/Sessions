package com.yugentech.sessions.ui.dash.util

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

fun defaultEnterTransition(
    duration: Int = 250,
    initialOffsetX: Int = 1000
): EnterTransition {
    return slideInHorizontally(
        animationSpec = tween(duration),
        initialOffsetX = { initialOffsetX }
    ) + fadeIn(animationSpec = tween(duration))
}

fun defaultExitTransition(
    duration: Int = 250,
    targetOffsetX: Int = -1000
): ExitTransition {
    return slideOutHorizontally(
        animationSpec = tween(duration),
        targetOffsetX = { targetOffsetX }
    ) + fadeOut(animationSpec = tween(duration))
}

fun defaultPopEnterTransition(
    duration: Int = 250,
    initialOffsetX: Int = -1000
): EnterTransition {
    return slideInHorizontally(
        animationSpec = tween(duration),
        initialOffsetX = { initialOffsetX }
    ) + fadeIn(animationSpec = tween(duration))
}

fun defaultPopExitTransition(
    duration: Int = 250,
    targetOffsetX: Int = 1000
): ExitTransition {
    return slideOutHorizontally(
        animationSpec = tween(duration),
        targetOffsetX = { targetOffsetX }
    ) + fadeOut(animationSpec = tween(duration))
}