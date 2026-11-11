package com.yugentech.sessions.ui.dash.homeScreen.layouts.studio

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
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.timer.config.TimerConfig
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.PlanChip
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.PlanSummaryChips
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.formatMinutes
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.planMinutes
import com.yugentech.sessions.ui.dash.mainScreen.components.pressClickable
import kotlinx.coroutines.delay
import java.util.Date

private val TileShape = RoundedCornerShape(24.dp)

/**
 * The plan before a session: focus, break and sets as three tiles you can tap to change, and
 * under them when you'd be done if you started now, and how long that is all told.
 */
@Composable
fun StudioPlan(
    config: TimerConfig,
    onFocus: () -> Unit,
    onBreak: () -> Unit,
    onSets: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val total = planMinutes(
        config.focusDuration, config.shortBreakDuration, config.longBreakDuration,
        config.targetSets, config.setsPerLongBreak, config.longBreakEnabled
    )
    // "Now" moves on, so the finish time stays true while the screen sits idle
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            now = System.currentTimeMillis()
        }
    }
    val finish = android.text.format.DateFormat.getTimeFormat(LocalContext.current)
        .format(Date(now + total * 60_000L))

    // Exactly the session card's height, so the swap between them never moves the dial
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(StudioCardHeight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PlanTile(stringResource(R.string.mode_focus), config.focusDuration, stringResource(R.string.unit_min), scheme.primary, onFocus, Modifier.weight(1f))
            PlanTile(stringResource(R.string.plan_break), config.shortBreakDuration, stringResource(R.string.unit_min), scheme.tertiary, onBreak, Modifier.weight(1f))
            PlanTile(stringResource(R.string.action_sets), config.targetSets, pluralStringResource(R.plurals.unit_sets, config.targetSets), scheme.secondary, onSets, Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
        // When you'd be done if you started now, and how long that is all told
        val totalText = formatMinutes(total)
        PlanSummaryChips(
            listOf(
                PlanChip(
                    icon = Icons.Outlined.Flag,
                    text = stringResource(R.string.plan_chip_finish, finish),
                    value = finish,
                    accent = scheme.tertiary
                ),
                PlanChip(
                    icon = Icons.Outlined.Schedule,
                    text = stringResource(R.string.plan_chip_total, totalText),
                    value = totalText,
                    accent = scheme.primary
                )
            )
        )
    }
}

@Composable
private fun PlanTile(
    label: String,
    value: Int,
    unit: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxHeight()
            .pressClickable(shape = TileShape, onClickLabel = stringResource(R.string.cd_change, label), onClick = onClick)
            .background(scheme.surfaceContainer, TileShape)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(6.dp).background(accent, CircleShape))
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = scheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            AnimatedContent(
                targetState = value,
                transitionSpec = {
                    val up = targetState > initialState
                    (slideInVertically(tween(260)) { if (up) it else -it } + fadeIn(tween(260)))
                        .togetherWith(slideOutVertically(tween(260)) { if (up) -it else it } + fadeOut(tween(160)))
                        .using(SizeTransform(clip = false))
                },
                label = "planTile$label"
            ) { v ->
                Text(
                    text = "$v",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = scheme.onSurface
                )
            }
            Text(
                text = unit,
                style = MaterialTheme.typography.labelLarge,
                color = scheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 3.dp, bottom = 5.dp)
            )
        }
    }
}
