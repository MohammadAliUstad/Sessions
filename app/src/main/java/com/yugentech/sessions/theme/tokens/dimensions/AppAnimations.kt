package com.yugentech.sessions.theme.tokens.dimensions

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

    // Transitions for AnimatedContent
    object Transitions {
        // Material's fade through, for content that replaces other content in the same place (a
        // label, an icon, a loading spinner): the old one fades out quickly while the new one
        // fades in, growing from 92%. Timed with the theme's Material 3 Expressive springs: the
        // fades on its effects springs (no bounce), the grow on its spatial spring (a slight
        // bounce). Springs can't be delayed, so the two overlap briefly at the start; the fast
        // spring clears the old one within about a tenth of a second.
        // Use: val fadeThrough = rememberExpressiveFadeThrough(), then transitionSpec = { fadeThrough() }.
        @OptIn(ExperimentalMaterial3ExpressiveApi::class)
        @Composable
        fun rememberExpressiveFadeThrough(): () -> ContentTransform {
            val motionScheme = MaterialTheme.motionScheme
            return remember(motionScheme) {
                val fadeOutSpec = motionScheme.fastEffectsSpec<Float>()
                val fadeInSpec = motionScheme.defaultEffectsSpec<Float>()
                val growSpec = motionScheme.defaultSpatialSpec<Float>()
                val transition: () -> ContentTransform = {
                    (fadeIn(fadeInSpec) + scaleIn(growSpec, initialScale = 0.92f)) togetherWith
                            fadeOut(fadeOutSpec)
                }
                transition
            }
        }
    }
}