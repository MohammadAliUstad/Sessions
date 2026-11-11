package com.yugentech.sessions.ui.dash.homeScreen.layouts.refined

import androidx.compose.ui.res.pluralStringResource
import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Schedule
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
import androidx.compose.ui.text.style.TextOverflow
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.PlanChip
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.PlanSummaryChips
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.formatMinutes
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.planMinutes
import com.yugentech.sessions.ui.dash.mainScreen.components.pressClickable

/**
 * The plan before a session starts: focus and break as two tiles side by side, each laid out like
 * the progress card that replaces it (a labelled badge on top, the number large at the bottom),
 * with a pencil saying it can be tapped to change. Under them, one quiet line with how many sets
 * that makes and roughly how long it all runs. The same height as the progress card, so the swap
 * doesn't jump.
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
    val totalMinutes = planMinutes(
        focusDurationMinutes, shortBreakDurationMinutes, longBreakDurationMinutes,
        targetSets, setsPerLongBreak, longBreakEnabled
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.components.imageSizeLarge)
            .padding(horizontal = MaterialTheme.spacing.m),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
        ) {
            PlanTile(
                label = stringResource(R.string.mode_focus),
                minutes = focusDurationMinutes,
                icon = Icons.Outlined.Timer,
                badgeColor = MaterialTheme.colorScheme.primaryContainer,
                badgeContent = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = onFocusClick,
                modifier = Modifier.weight(1f)
            )
            PlanTile(
                label = stringResource(R.string.plan_break),
                minutes = shortBreakDurationMinutes,
                icon = Icons.Outlined.Coffee,
                badgeColor = MaterialTheme.colorScheme.tertiaryContainer,
                badgeContent = MaterialTheme.colorScheme.onTertiaryContainer,
                onClick = onShortBreakClick,
                modifier = Modifier.weight(1f)
            )
        }

        // The whole plan at a glance: how many sets, and how long it all runs
        val total = formatMinutes(totalMinutes)
        PlanSummaryChips(
            listOf(
                PlanChip(
                    icon = Icons.Outlined.Layers,
                    text = pluralStringResource(R.plurals.sets_count, targetSets, targetSets),
                    value = "$targetSets",
                    accent = MaterialTheme.colorScheme.secondary
                ),
                PlanChip(
                    icon = Icons.Outlined.Schedule,
                    text = stringResource(R.string.plan_chip_total, total),
                    value = total,
                    accent = MaterialTheme.colorScheme.primary
                )
            )
        )
    }
}

/**
 * One length in the plan, as its own tile: a round badge with the mode's icon and its name on
 * top, a pencil in the corner, and the minutes large at the bottom.
 */
@Composable
private fun PlanTile(
    label: String,
    minutes: Int,
    icon: ImageVector,
    badgeColor: Color,
    badgeContent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(MaterialTheme.corners.large)
    Column(
        modifier = modifier
            .fillMaxHeight()
            .pressClickable(
                shape = shape,
                onClickLabel = stringResource(R.string.cd_change_length, label),
                onClick = onClick
            )
            .background(MaterialTheme.colorScheme.surfaceContainer, shape)
            .padding(MaterialTheme.spacing.m),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(MaterialTheme.components.buttonSmall)
                    .background(badgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.icons.small),
                    tint = badgeContent
                )
            }
            Spacer(Modifier.width(MaterialTheme.spacing.s))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                modifier = Modifier.size(MaterialTheme.icons.small),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
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
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = stringResource(R.string.unit_min),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = MaterialTheme.spacing.xxs, bottom = MaterialTheme.spacing.xsSmall)
            )
        }
    }
}
