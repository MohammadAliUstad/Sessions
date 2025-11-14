package com.yugentech.sessions.theme.tokens.dimensions.animations

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing

data class AnimationEasingTokens(
    val linear: Easing = LinearEasing,
    val standard: Easing = EaseInOutCubic,
    val accelerate: Easing = CubicBezierEasing(0.3f, 0.0f, 1f, 1f),
    val decelerate: Easing = CubicBezierEasing(0f, 0f, 0f, 1f),
    val emphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f),
    val emphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
)