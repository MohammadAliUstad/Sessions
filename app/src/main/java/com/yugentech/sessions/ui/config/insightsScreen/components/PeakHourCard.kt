package com.yugentech.sessions.ui.config.insightsScreen.components

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import android.text.format.DateFormat
import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun PeakHourCard(
    peakHour: Int?,
    shape: Shape = RoundedCornerShape(MaterialTheme.corners.large)
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(MaterialTheme.spacing.m),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (peakHour != null) {
                // The hour as the language writes it ("3 PM", "15 h", "午後3時"...)
                val locale = LocalConfiguration.current.locales[0]
                val hourText = remember(peakHour, locale) {
                    val pattern = DateFormat.getBestDateTimePattern(locale, "j")
                    java.time.LocalTime.of(peakHour, 0).format(java.time.format.DateTimeFormatter.ofPattern(pattern, locale))
                }

                Text(
                    text = hourText,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )

                Spacer(Modifier.width(MaterialTheme.spacing.m))

                Column {
                    Text(
                        text = stringResource(R.string.peak_title),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stringResource(R.string.peak_subtitle),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                Text(
                    text = "--",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.5f)
                )

                Spacer(Modifier.width(MaterialTheme.spacing.m))

                Column {
                    Text(
                        text = stringResource(R.string.peak_empty),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stringResource(R.string.peak_empty_subtitle),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}