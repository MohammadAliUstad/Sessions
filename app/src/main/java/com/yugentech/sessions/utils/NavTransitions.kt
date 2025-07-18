package com.yugentech.sessions.utils

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

private const val AnimationDuration = 300

val defaultEnterTransition: EnterTransition
    get() = slideInHorizontally(
        animationSpec = tween(durationMillis = AnimationDuration)
    ) { fullWidth -> fullWidth } + fadeIn(animationSpec = tween(durationMillis = AnimationDuration))

val defaultExitTransition: ExitTransition
    get() = slideOutHorizontally(
        animationSpec = tween(durationMillis = AnimationDuration)
    ) { fullWidth -> -fullWidth } + fadeOut(animationSpec = tween(durationMillis = AnimationDuration))

val defaultPopEnterTransition: EnterTransition
    get() = slideInHorizontally(
        animationSpec = tween(durationMillis = AnimationDuration)
    ) { fullWidth -> -fullWidth } + fadeIn(animationSpec = tween(durationMillis = AnimationDuration))

val defaultPopExitTransition: ExitTransition
    get() = slideOutHorizontally(
        animationSpec = tween(durationMillis = AnimationDuration)
    ) { fullWidth -> fullWidth } + fadeOut(animationSpec = tween(durationMillis = AnimationDuration))