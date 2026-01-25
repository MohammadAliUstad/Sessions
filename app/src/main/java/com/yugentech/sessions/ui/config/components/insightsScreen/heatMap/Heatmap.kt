package com.yugentech.sessions.ui.config.components.insightsScreen.heatMap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.spacing
import java.time.LocalDate

@Composable
fun Heatmap(
    data: Map<LocalDate, Int>,
    modifier: Modifier = Modifier
) {
    val heatmapWeeks = remember(data) { generateHeatmapData(data) }
    val tokens = MaterialTheme.spacing
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        listState.scrollToItem(heatmapWeeks.size)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            Modifier.padding(tokens.m)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Consistency Heatmap",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Less",
                        style = MaterialTheme.typography.labelSmall
                    )

                    HeatmapCell(1)
                    HeatmapCell(3)
                    HeatmapCell(5)

                    Text(
                        text = "More",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(Modifier.height(tokens.m))

            Row {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.size(
                                height = 20.dp,
                                width = 16.dp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(Modifier.width(tokens.s))

                LazyRow(
                    state = listState,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(heatmapWeeks) { week ->
                        Column {
                            Box(modifier = Modifier.height(20.dp)) {
                                if (week.firstDayOfMonth != null) {
                                    Text(
                                        text = week.firstDayOfMonth,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        softWrap = false
                                    )
                                }
                            }

                            Spacer(Modifier.height(4.dp))

                            HeatmapWeekColumn(week.days)
                        }
                    }
                }
            }
        }
    }
}