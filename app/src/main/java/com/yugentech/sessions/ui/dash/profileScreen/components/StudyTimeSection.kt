package com.yugentech.sessions.ui.dash.profileScreen.components

import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.elevation
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun StudyTimeSection(
    formattedTime: String
) {
    val hasTime = formattedTime != "0"

    val timeLabels = remember {
        listOf(
            R.string.study_label_in_the_zone,
            R.string.study_label_locked_in,
            R.string.study_label_deep_work,
            R.string.study_label_invested,
            R.string.study_label_grinded,
            R.string.study_label_well_spent,
            R.string.study_label_committed,
            R.string.study_label_logged
        )
    }

    val labelRes = remember { timeLabels.random() }
    val label = stringResource(if (hasTime) labelRes else R.string.study_label_none).uppercase()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(MaterialTheme.corners.medium),
        tonalElevation = MaterialTheme.elevation.level2
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.m),
            horizontalArrangement = if (hasTime) Arrangement.SpaceBetween else Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f, fill = false)
            )

            if (hasTime) {
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
        }
    }
}