package com.yugentech.sessions.ui.auth.components.buttons

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
import com.yugentech.sessions.R
import com.yugentech.sessions.ui.components.TintedLottieIcon
import com.yugentech.sessions.R.string.continue_with_google
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.theme.tokens.strokes

/** How long the label and spinner take to slide past each other. */
private const val GOOGLE_BUTTON_MOTION_MS = 350

/** Material 3's emphasized easing (motion.easing.emphasized). */
private val EmphasizedEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

/**
 * "Continue with Google", with Google's "G". While [isLoading] the label swaps to a spinner with
 * Amatyaa's vertical swap: the label moves up and fades out while the spinner rises in from below
 * and fades in (the other way round when loading ends). They travel 20 dp, about half the
 * button's height, and the button's own shape clips them, so they pass through its edge rather
 * than vanishing mid-way.
 */
@Composable
fun GoogleSignInButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = !isLoading,
    label: String = stringResource(continue_with_google)
) {
    AuthOptionButton(
        icon = { size ->
            Icon(
                painter = painterResource(R.drawable.ic_google_icon),
                contentDescription = null,
                // Google's own four colours, untinted.
                tint = Color.Unspecified,
                modifier = Modifier.size(size)
            )
        },
        label = label,
        isLoading = isLoading,
        enabled = enabled,
        onClick = onClick,
        modifier = modifier
    )
}

/**
 * "Guest": starts Sessions without an account. Same look as [GoogleSignInButton], to sit beside it,
 * with a little ghost that keeps looping its hover-and-pinch, tinted to match the label.
 *
 * Tapping it sets the ghost free: it pops out of the button, swoops up the screen in a wobbly
 * zig-zag, growing and turning to the primary colour with two fading after-images behind it, and
 * vanishes off the top. Only then does [onClick] run and the app open.
 */
@Composable
fun GuestSignInButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = !isLoading
) {
    val currentOnClick by rememberUpdatedState(onClick)
    // Once tapped the ghost has gone for good: the button stays empty until the app opens and
    // this screen leaves, rather than the ghost popping back in at the end
    var flying by remember { mutableStateOf(false) }
    var gone by remember { mutableStateOf(false) }
    val flight = remember { Animatable(0f) }
    // Where the ghost sits in the button, in window coordinates: the flight starts there
    var origin by remember { mutableStateOf(Offset.Zero) }
    var iconSize by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(flying) {
        if (!flying) return@LaunchedEffect
        flight.snapTo(0f)
        flight.animateTo(1f, tween(GHOST_FLIGHT_MS, easing = LinearEasing))
        flying = false
        currentOnClick()
    }

    // If the app didn't open after all (the button is usable again with no flight running),
    // bring the ghost back
    LaunchedEffect(gone, flying, enabled) {
        if (gone && !flying && enabled) {
            delay(GHOST_RETURN_GRACE_MS)
            gone = false
        }
    }

    // One box around the button and the flight: a Popup leaves an empty anchor where it's
    // declared, and as a second child of the caller's Row it would take a gap of its own and
    // squeeze the buttons while the ghost is away
    Box(modifier = modifier) {
    AuthOptionButton(
        icon = { size ->
            TintedLottieIcon(
                animation = R.raw.guest_ghost,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(size)
                    .onGloballyPositioned {
                        origin = it.positionInWindow()
                        iconSize = it.size
                    }
                    // The one in the button hides while its copy is out flying, and after
                    .graphicsLayer { alpha = if (gone) 0f else 1f }
            )
        },
        label = stringResource(R.string.guest_button),
        isLoading = isLoading,
        // Stays enabled-looking during the flight (a second tap is just ignored), so the
        // button doesn't change while the ghost is away
        enabled = enabled,
        onClick = {
            if (!gone) {
                gone = true
                flying = true
            }
        },
        modifier = Modifier
    )

    if (flying && iconSize != IntSize.Zero) {
        GhostFlight(progress = { flight.value }, origin = origin, iconSize = iconSize)
    }
    }
}

/** The whole flight, from the tap to the app opening. */
private const val GHOST_FLIGHT_MS = 1150

/** How long the button may sit idle after the flight before the ghost comes back (app didn't open). */
private const val GHOST_RETURN_GRACE_MS = 1500L

/**
 * The flying ghost, drawn in a full-window popup so it can leave the button and cross the screen.
 * [progress] runs 0 to 1: a quick pop in place, then the swoop up and away.
 */
@Composable
private fun GhostFlight(progress: () -> Float, origin: Offset, iconSize: IntSize) {
    val density = LocalDensity.current
    val sizeDp = with(density) { iconSize.width.toDp() }
    val swing = with(density) { 56.dp.toPx() }
    val hop = with(density) { 10.dp.toPx() }
    // Far enough up to clear the top of the screen even at full size
    val rise = origin.y + with(density) { 160.dp.toPx() }
    val start = MaterialTheme.colorScheme.onSurfaceVariant
    val end = MaterialTheme.colorScheme.primary

    Popup(
        popupPositionProvider = WindowOriginPosition,
        properties = PopupProperties(focusable = false, clippingEnabled = false)
    ) {
        Box(Modifier.fillMaxSize()) {
            // Two after-images trailing behind, then the ghost itself on top
            listOf(0.08f to 0.2f, 0.04f to 0.4f, 0f to 1f).forEach { (lag, opacity) ->
                val pose = { ghostPose(progress() - lag, swing, hop, rise) }
                TintedLottieIcon(
                    animation = R.raw.guest_ghost,
                    color = lerp(start, end, (progress() * 1.6f).coerceIn(0f, 1f)),
                    modifier = Modifier
                        .offset { origin.round() }
                        .size(sizeDp)
                        .graphicsLayer {
                            val p = pose()
                            translationX = p.x
                            translationY = p.y
                            scaleX = p.scale
                            scaleY = p.scale
                            rotationZ = p.rotation
                            alpha = p.alpha * opacity
                        }
                )
            }
        }
    }
}

private class GhostPose(val x: Float, val y: Float, val scale: Float, val rotation: Float, val alpha: Float)

/** Where the ghost is at [p] (0 to 1); before 0 it hasn't left yet (for the after-images). */
private fun ghostPose(p: Float, swing: Float, hop: Float, rise: Float): GhostPose {
    if (p <= 0f) return GhostPose(0f, 0f, 1f, 0f, 0f)
    val pop = 0.16f
    if (p < pop) {
        // Startled: a springy jump in place, a size up and a little shiver
        val t = p / pop
        val bounce = sin(t * PI.toFloat())
        return GhostPose(
            x = 0f,
            y = -hop * bounce,
            scale = 1f + 0.7f * FastOutSlowInEasing.transform(t),
            rotation = sin(t * 6 * PI.toFloat()) * 10f,
            alpha = 1f
        )
    }
    // Then off it goes: accelerating upward, swinging side to side with a wobble, growing as it
    // comes "closer", and fading out near the top
    val t = (p - pop) / (1f - pop)
    val climb = t * t * (1.4f - 0.4f * t)
    return GhostPose(
        x = sin(t * 2.5f * PI.toFloat()) * swing * (1f - 0.35f * t),
        y = -hop - rise * climb,
        scale = 1.7f + 2.6f * t,
        rotation = sin(t * 5f * PI.toFloat()) * 18f,
        alpha = if (t < 0.65f) 1f else (1f - (t - 0.65f) / 0.35f).coerceIn(0f, 1f)
    )
}

/** Puts a popup at the window's top-left corner, so it covers the whole window. */
private object WindowOriginPosition : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset = IntOffset.Zero
}

@Composable
private fun AuthOptionButton(
    icon: @Composable (size: Dp) -> Unit,
    label: String,
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val travel = with(LocalDensity.current) { 20.dp.roundToPx() }

    Surface(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(MaterialTheme.components.buttonMedium),
        shape = RoundedCornerShape(MaterialTheme.corners.extraLarge),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(contentAlignment = Alignment.Center) {
            AnimatedContent(
                targetState = isLoading,
                transitionSpec = {
                    // Loading: up and away. Done: back down.
                    val direction = if (targetState) 1 else -1
                    val enter = slideInVertically(tween(GOOGLE_BUTTON_MOTION_MS, easing = EmphasizedEasing)) { direction * travel } +
                        fadeIn(tween(GOOGLE_BUTTON_MOTION_MS / 2, delayMillis = GOOGLE_BUTTON_MOTION_MS / 6))
                    val exit = slideOutVertically(tween(GOOGLE_BUTTON_MOTION_MS, easing = EmphasizedEasing)) { -direction * travel } +
                        fadeOut(tween(GOOGLE_BUTTON_MOTION_MS / 3))
                    (enter togetherWith exit).using(SizeTransform(clip = false))
                },
                contentAlignment = Alignment.Center,
                label = "googleLoading"
            ) { loading ->
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(MaterialTheme.icons.mediumSmall),
                        color = MaterialTheme.colorScheme.onSurface,
                        strokeWidth = MaterialTheme.strokes.medium
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        icon(MaterialTheme.icons.mediumSmall)

                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.s))

                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
