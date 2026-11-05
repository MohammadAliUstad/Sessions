package com.yugentech.sessions.ui.dash.homeScreen.layouts.refined

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.formatMinutes
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.planMinutes
import com.yugentech.sessions.ui.dash.mainScreen.components.pressClickable

/**
 * The plan before a session starts, as one card: focus and break lengths side by side (tap
 * either to change it), and underneath how many sets that makes and roughly how long it all runs.
 * The same height as the progress card that replaces it, so the swap doesn't jump.
 */
@Composable
fun RefinedPlanCard(
    focusDurationMinutes: Int,
    shortBreakDurationMinutes: Int,
    onFocusClick: () -> Unit,
    onShortBreakClick: () -> Unit,
    targetSets: Int = 1,
    longBreakDurationMinutes: Int = 0,
    setsPerLongBreak: Int = 0,
    longBreakEnabled: Boolean = false
) {
    val shape = RoundedCornerShape(MaterialTheme.corners.large)
    val totalMinutes = planMinutes(
        focusDurationMinutes, shortBreakDurationMinutes, longBreakDurationMinutes,
        targetSets, setsPerLongBreak, longBreakEnabled
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.components.imageSizeLarge)
            .padding(horizontal = MaterialTheme.spacing.m)
            .background(MaterialTheme.colorScheme.surfaceContainer, shape)
            .padding(MaterialTheme.spacing.s)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlanItem(
                label = "Focus",
                minutes = focusDurationMinutes,
                icon = Icons.Outlined.Timer,
                accent = MaterialTheme.colorScheme.primary,
                onClick = onFocusClick,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight(0.6f)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            )
            PlanItem(
                label = "Break",
                minutes = shortBreakDurationMinutes,
                icon = Icons.Outlined.Coffee,
                accent = MaterialTheme.colorScheme.tertiary,
                onClick = onShortBreakClick,
                modifier = Modifier.weight(1f)
            )
        }

        // The whole plan at a glance
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(MaterialTheme.corners.medium))
                .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.s),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Layers,
                contentDescription = null,
                modifier = Modifier.size(MaterialTheme.icons.small),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(MaterialTheme.spacing.xs))
            Text(
                text = "${targetSets} ${if (targetSets == 1) "set" else "sets"} · about ${formatMinutes(totalMinutes)}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** One length in the plan: a small labelled icon, then the minutes in large type. */
@Composable
private fun PlanItem(
    label: String,
    minutes: Int,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = MaterialTheme.spacing.xs)
            .pressClickable(
                shape = RoundedCornerShape(MaterialTheme.corners.medium),
                onClickLabel = "Change $label length",
                onClick = onClick
            )
            .padding(MaterialTheme.spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(MaterialTheme.icons.small),
                tint = accent
            )
            Spacer(Modifier.width(MaterialTheme.spacing.xs))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(verticalAlignment = Alignment.Bottom) {
            AnimatedContent(
                targetState = minutes,
                transitionSpec = {
                    val up = targetState > initialState
                    (slideInVertically(tween(260)) { if (up) it else -it } + fadeIn(tween(260)))
                        .togetherWith(slideOutVertically(tween(260)) { if (up) -it else it } + fadeOut(tween(160)))
                        .using(SizeTransform(clip = false))
                },
                label = "planMinutes$label"
            ) { value ->
                Text(
                    text = "$value",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "min",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = MaterialTheme.spacing.xxs, bottom = MaterialTheme.spacing.xsSmall)
            )
        }
    }
}
