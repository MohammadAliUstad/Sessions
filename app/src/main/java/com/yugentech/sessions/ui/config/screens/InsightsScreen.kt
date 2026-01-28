package com.yugentech.sessions.ui.config.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.config.components.insightsScreen.ConsistencyCard
import com.yugentech.sessions.ui.config.components.insightsScreen.EmptyDistributionPlaceholder
import com.yugentech.sessions.ui.config.components.insightsScreen.InsightSectionHeader
import com.yugentech.sessions.ui.config.components.insightsScreen.MetricCard
import com.yugentech.sessions.ui.config.components.insightsScreen.PeakHourCard
import com.yugentech.sessions.ui.config.components.insightsScreen.TaskDistributionList
import com.yugentech.sessions.ui.config.components.insightsScreen.WeeklyRhythmChart
import com.yugentech.sessions.ui.config.components.insightsScreen.heatMap.Heatmap
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InsightsScreen(
    totalTime: String,
    streakCount: Int,
    taskDistribution: Map<String, Int>,
    dailyVolume: Map<Int, Int>,
    heatmapHistory: Map<LocalDate, Int>,
    peakHour: Int?,
    onBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val topTask = taskDistribution.maxByOrNull { it.value }?.key ?: "No data yet"

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("Insights")
                        Text(
                            "Your productivity patterns",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(MaterialTheme.spacing.m),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
        ) {
            item {
                MetricCard(
                    title = "Total Focus Time",
                    value = totalTime,
                    subtitle = "Cumulative time across all sessions",
                    icon = Icons.Default.Timer
                )
            }

            if (taskDistribution.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(MaterialTheme.corners.large),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(MaterialTheme.spacing.m),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(MaterialTheme.icons.medium)
                            )

                            Spacer(Modifier.width(MaterialTheme.spacing.m))

                            Column {
                                Text(
                                    "Primary Focus",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                        alpha = 0.7f
                                    )
                                )

                                Text(
                                    topTask,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {
                PeakHourCard(peakHour = peakHour)
            }


            item {
                Heatmap(data = heatmapHistory)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(MaterialTheme.corners.large),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(
                        Modifier.padding(MaterialTheme.spacing.m)
                    ) {
                        InsightSectionHeader(
                            title = "Momentum",
                            subtitle = "Your consistency streak"
                        )

                        Spacer(Modifier.height(MaterialTheme.spacing.s))

                        ConsistencyCard(
                            streakCount = streakCount
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(MaterialTheme.corners.large),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(
                        Modifier.padding(MaterialTheme.spacing.m)
                    ) {
                        InsightSectionHeader(
                            title = "Weekly Rhythm",
                            subtitle = "Session volume by day of week"
                        )

                        Spacer(Modifier.height(MaterialTheme.spacing.m))

                        if (taskDistribution.isEmpty()) {
                            EmptyDistributionPlaceholder(
                                "Complete a session to see volume"
                            )
                        } else {
                            WeeklyRhythmChart(
                                dailyVolume = dailyVolume
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(MaterialTheme.corners.large),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(
                        Modifier.padding(MaterialTheme.spacing.m)
                    ) {
                        InsightSectionHeader(
                            title = "Task Allocation",
                            subtitle = "How your time is distributed"
                        )

                        Spacer(Modifier.height(MaterialTheme.spacing.s))

                        if (taskDistribution.isEmpty()) {
                            EmptyDistributionPlaceholder(
                                "Complete a session to see distribution"
                            )
                        } else {
                            TaskDistributionList(
                                taskDistribution = taskDistribution
                            )
                        }
                    }
                }
            }
        }
    }
}