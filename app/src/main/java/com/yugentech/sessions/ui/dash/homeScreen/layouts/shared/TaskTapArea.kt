package com.yugentech.sessions.ui.dash.homeScreen.layouts.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing

/** Room between the task text and the side edges of its tap area (and so its ripple). */
val TaskTapInset = 12.dp

/**
 * The tap area behind the task name, shared by Refined and Studio so its press ripple sits the
 * same way in both: a medium rounded corner, the text [TaskTapInset] in from the sides and a
 * small gap above and below. A plain ripple, with no scale or bounce on press.
 *
 * Callers start their row short of the usual margin by [TaskTapInset], so the text itself still
 * lines up with the content below while the ripple gets room around it.
 */
@Composable
fun Modifier.taskTapArea(enabled: Boolean, onClick: () -> Unit): Modifier =
    this
        .clip(RoundedCornerShape(MaterialTheme.corners.medium))
        .clickable(enabled = enabled, onClickLabel = "Change task", onClick = onClick)
        .padding(horizontal = TaskTapInset, vertical = MaterialTheme.spacing.s)
