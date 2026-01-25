package com.yugentech.sessions.theme.tokens.dimensions

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.unit.dp

object AppAnimations {

    // Standard duration constants (in milliseconds) for consistent animation speeds
    object Durations {
        const val Immediate = 0
        const val Micro = 50
        const val Rapid = 100
        const val Fast = 150
        const val Base = 200
        const val Smooth = 250
        const val Standard = 300
        const val Emphasized = 400
        const val Complex = 500
        const val Slow = 700
        const val Extended = 1000
        const val Delay = 1200
        const val Sustained = 1500
        const val Delayed = 2000
        const val Prolonged = 3000
        const val RepeatDelay = 2000
        const val Carousel = 20000
    }

    // Standard easing curves to define how animations accelerate and decelerate
    object Easings {
        val Linear: Easing = LinearEasing
        val Standard: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
        val Simple: Easing = EaseInOutCubic
        val Emphasized: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
        val EmphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
        val EmphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
        val Decelerate: Easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
        val Accelerate: Easing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
    }

    // Physical constants for motion-based spatial changes
    object Motion {
        val Velocity = 30.dp
    }
}