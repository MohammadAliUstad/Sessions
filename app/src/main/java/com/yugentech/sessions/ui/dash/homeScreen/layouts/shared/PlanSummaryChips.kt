package com.yugentech.sessions.ui.dash.homeScreen.layouts.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.spacing

/**
 * One fact about the plan: [text] as the phone's language words it ("4 sets", "Done by 5:40 PM"),
 * with [value] (the part inside it that matters) picked out in bold.
 */
data class PlanChip(
    val icon: ImageVector,
    val text: String,
    val value: String,
    val accent: Color
)

/**
 * The plan's summary under its tiles, shared by Refined and Studio: small pills side by side,
 * each with its icon in a tinted dot and the number in bold, the words around it quieter.
 */
@Composable
fun PlanSummaryChips(chips: List<PlanChip>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        chips.forEach { chip ->
            PlanChipPill(chip, Modifier.weight(1f, fill = false))
        }
    }
}

@Composable
private fun PlanChipPill(chip: PlanChip, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .background(scheme.surfaceContainerHigh, CircleShape)
            .padding(start = MaterialTheme.spacing.xs, end = MaterialTheme.spacing.sm)
            .padding(vertical = MaterialTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(chip.accent.copy(alpha = 0.16f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = chip.icon,
                contentDescription = null,
                tint = chip.accent,
                modifier = Modifier.size(13.dp)
            )
        }
        Spacer(Modifier.width(MaterialTheme.spacing.xsSmall))
        Text(
            text = buildAnnotatedString {
                append(chip.text)
                val at = chip.text.indexOf(chip.value)
                if (chip.value.isNotEmpty() && at >= 0) {
                    addStyle(
                        SpanStyle(fontWeight = FontWeight.Bold, color = scheme.onSurface),
                        at,
                        at + chip.value.length
                    )
                }
            },
            style = MaterialTheme.typography.labelLarge,
            color = scheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
