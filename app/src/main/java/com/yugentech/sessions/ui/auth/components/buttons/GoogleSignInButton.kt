package com.yugentech.sessions.ui.auth.components.buttons

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.CubicBezierEasing
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.R
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
    enabled: Boolean = !isLoading
) {
    val travel = with(LocalDensity.current) { 20.dp.roundToPx() }

    Surface(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = Modifier
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
                        Icon(
                            painter = painterResource(R.drawable.ic_google_icon),
                            contentDescription = null,
                            // Google's own four colours, untinted.
                            tint = Color.Unspecified,
                            modifier = Modifier.size(MaterialTheme.icons.mediumSmall)
                        )

                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.s))

                        Text(
                            text = stringResource(continue_with_google),
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
