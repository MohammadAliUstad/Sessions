package com.yugentech.sessions.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import kotlinx.coroutines.launch
import timber.log.Timber

const val SLOW_FADE_MS = 550

class ThemeRevealController internal constructor(
    private val onFade: (change: () -> Unit, durationMs: Int) -> Unit
) {
    fun fade(change: () -> Unit, durationMs: Int) = onFade(change, durationMs)
}

val LocalThemeReveal = staticCompositionLocalOf<ThemeRevealController?> { null }

@Composable
fun ThemeRevealHost(content: @Composable () -> Unit) {
    val layer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()
    var snapshot by remember { mutableStateOf<ImageBitmap?>(null) }
    val progress = remember { Animatable(0f) }

    val controller = remember {
        ThemeRevealController { change, durationMs ->
            if (snapshot != null) {
                change()
                return@ThemeRevealController
            }
            scope.launch {
                val frozen = runCatching { layer.toImageBitmap() }
                    .onFailure { Timber.w(it, "Couldn't capture the screen for the theme fade") }
                    .getOrNull()
                if (frozen == null) {
                    change()
                    return@launch
                }
                progress.snapTo(0f)
                snapshot = frozen
                change()
                try {
                    progress.animateTo(1f, tween(durationMs, easing = FastOutSlowInEasing))
                } finally {
                    snapshot = null
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithContent {
                layer.record { this@drawWithContent.drawContent() }
                drawLayer(layer)
                snapshot?.let { drawImage(it, alpha = 1f - progress.value) }
            }
    ) {
        CompositionLocalProvider(LocalThemeReveal provides controller, content = content)
    }
}