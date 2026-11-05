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
private const val FADE_MS = 350

/** The fade the Appearance screen uses for colour theme, theme mode and AMOLED changes. */
const val SLOW_FADE_MS = 550

/** How a theme change shows: a circle opening from a point, or the whole screen fading across. */
internal enum class RevealStyle { Circle, Fade }

/**
 * Runs a theme change behind an animation. Both freeze what's on screen and apply the change
 * underneath, then:
 *  - [reveal] (light and dark): opens a growing circle in the frozen picture from [Offset] (in
 *    root coordinates, the centre of the button that was tapped) until the new look has taken over.
 *  - [fade] (colour theme, theme mode, AMOLED): fades the frozen picture out over [durationMs],
 *    so the new colours simply dissolve in.
 */
class ThemeRevealController internal constructor(
    private val onChange: (style: RevealStyle, origin: Offset, durationMs: Int, change: () -> Unit) -> Unit
) {
    fun reveal(origin: Offset, change: () -> Unit) = onChange(RevealStyle.Circle, origin, REVEAL_MS, change)

    fun fade(change: () -> Unit, durationMs: Int = FADE_MS) =
        onChange(RevealStyle.Fade, Offset.Zero, durationMs, change)
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
    var style by remember { mutableStateOf(RevealStyle.Circle) }
    val progress = remember { Animatable(0f) }

    val controller = remember {
        ThemeRevealController { kind, at, durationMs, change ->
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
                style = kind
                progress.snapTo(0f)
                change()
                try {
                    progress.animateTo(1f, tween(durationMillis = durationMs, easing = FastOutSlowInEasing))
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
                    when (style) {
                        // The old look fading away over the new one
                        RevealStyle.Fade -> drawImage(old, alpha = 1f - progress.value)
                        RevealStyle.Circle -> {
                            val maxRadius = hypot(
                                max(origin.x, size.width - origin.x),
                                max(origin.y, size.height - origin.y)
                            )
                            val hole = Path().apply { addOval(Rect(origin, maxRadius * progress.value)) }
                            clipPath(hole, clipOp = ClipOp.Difference) { drawImage(old) }
                        }
                    }
                }
            }
    ) {
        CompositionLocalProvider(LocalThemeReveal provides controller, content = content)
    }
}
