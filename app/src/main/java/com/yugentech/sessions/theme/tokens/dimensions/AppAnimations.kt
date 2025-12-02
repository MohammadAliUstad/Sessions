package com.yugentech.sessions.theme.tokens.dimensions

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing

object AppAnimations {

    object Durations {
        const val SHORT = 150
        const val STANDARD = 300
        const val MEDIUM = 400
        const val LONG = 500
        const val EXTRALONG = 700
    }

    object Easings {
        val Linear = LinearEasing
        val Standard = FastOutSlowInEasing
        val Emphasized = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    }
}