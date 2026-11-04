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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.hypot
import kotlin.math.max

private const val REVEAL_MS = 600

/**
 * Runs a theme change behind a circular wipe. [reveal] freezes what's on screen, applies the
 * change underneath, then opens a growing circle in the frozen picture from [Offset] (in root
 * coordinates) until the new look has taken over.
 */
class ThemeRevealController internal constructor(
    private val onReveal: (origin: Offset, change: () -> Unit) -> Unit
) {
    fun reveal(origin: Offset, change: () -> Unit) = onReveal(origin, change)
}

/** Null outside [ThemeRevealHost], in which case callers just apply the change. */
val LocalThemeReveal = staticCompositionLocalOf<ThemeRevealController?> { null }

/** Wraps the whole app, so the wipe covers every pixel. Put it inside [SessionsTheme]'s content. */
@Composable
fun ThemeRevealHost(content: @Composable () -> Unit) {
    val layer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()
    var snapshot by remember { mutableStateOf<ImageBitmap?>(null) }
    var origin by remember { mutableStateOf(Offset.Zero) }
    val progress = remember { Animatable(0f) }

    val controller = remember {
        ThemeRevealController { at, change ->
            // A second tap mid-wipe just applies; stacking snapshots would show a stale frame.
            if (snapshot != null) {
                change()
                return@ThemeRevealController
            }
            scope.launch {
                val frozen = runCatching { layer.toImageBitmap() }
                    .onFailure { Timber.w(it, "Couldn't capture the screen for the theme reveal") }
                    .getOrNull()
                if (frozen == null) {
                    change()
                    return@launch
                }
                snapshot = frozen
                origin = at
                progress.snapTo(0f)
                change()
                try {
                    progress.animateTo(1f, tween(durationMillis = REVEAL_MS, easing = FastOutSlowInEasing))
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
                snapshot?.let { old ->
                    val maxRadius = hypot(
                        max(origin.x, size.width - origin.x),
                        max(origin.y, size.height - origin.y)
                    )
                    val hole = Path().apply { addOval(Rect(origin, maxRadius * progress.value)) }
                    clipPath(hole, clipOp = ClipOp.Difference) { drawImage(old) }
                }
            }
    ) {
        CompositionLocalProvider(LocalThemeReveal provides controller, content = content)
    }
}
