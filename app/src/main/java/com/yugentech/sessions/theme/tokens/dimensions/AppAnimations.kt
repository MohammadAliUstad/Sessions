package com.yugentech.sessions.theme.tokens.dimensions

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing

object AppAnimations {

    object Durations {
        const val Immediate = 0
        const val Rapid = 100
        const val Fast = 150
        const val Base = 200
        const val Smooth = 250
        const val Standard = 300
        const val Emphasized = 400
        const val Complex = 500
        const val Carousel = 20000
    }

    object Easings {
        val Linear: Easing = LinearEasing
        val Standard: Easing = EaseInOutCubic
        val Accelerate: Easing = CubicBezierEasing(0.3f, 0.0f, 1f, 1f)
        val Decelerate: Easing = CubicBezierEasing(0f, 0f, 0f, 1f)
        val EmphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
        val EmphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
    }
}