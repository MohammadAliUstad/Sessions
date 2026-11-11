package com.yugentech.sessions.ui.config.insightsScreen

import com.yugentech.sessions.ui.dash.util.displayTaskName
import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box // Added
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues // Added
import androidx.compose.foundation.layout.calculateEndPadding // Added
import androidx.compose.foundation.layout.calculateStartPadding // Added
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars // Added
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
// Removed statusBars import
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalLayoutDirection // Added
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.mainScreen.components.itemShape
import com.yugentech.sessions.ui.config.insightsScreen.components.ConsistencyCard
import com.yugentech.sessions.ui.config.insightsScreen.components.EmptyDistributionPlaceholder
import com.yugentech.sessions.ui.config.insightsScreen.components.InsightSectionHeader
import com.yugentech.sessions.ui.config.insightsScreen.components.MetricCard
import com.yugentech.sessions.ui.config.insightsScreen.components.PeakHourCard
import com.yugentech.sessions.ui.config.insightsScreen.components.TaskDistributionList
import com.yugentech.sessions.ui.config.insightsScreen.components.WeeklyRhythmChart
import com.yugentech.sessions.ui.config.insightsScreen.components.heatMap.Heatmap
import java.time.LocalDate

@Composable
fun InsightsScreen(
    totalTime: String,
    streakCount: Int,
    taskDistribution: Map<String, Int>,
    dailyVolume: Map<Int, Int>,
    heatmapHistory: Map<LocalDate, Int>,
    focusTimeByDate: Map<LocalDate, Long>,
    peakHour: Int?,
    onBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val topTask = taskDistribution.maxByOrNull { it.value }?.key?.let { displayTaskName(it) }
        ?: stringResource(R.string.insights_no_data)

    // 1. Get LayoutDirection and NavBar padding manually
    val layoutDirection = LocalLayoutDirection.current
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), // 2. Moved nestedScroll here
        // 3. Removed contentWindowInsets = WindowInsets.statusBars
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.profile_insights))
                        Text(
                            stringResource(R.string.insights_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        // 4. Wrap in Box to handle Top Padding separately
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    bottom = navBarPadding.calculateBottomPadding() + 8.dp,
                    start = MaterialTheme.spacing.m + paddingValues.calculateStartPadding(layoutDirection),
                    end = MaterialTheme.spacing.m + paddingValues.calculateEndPadding(layoutDirection)
                ),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
            ) {
                item {
                    val topSectionCount = if (taskDistribution.isNotEmpty()) 3 else 2

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        MetricCard(
                            title = stringResource(R.string.insights_total_title),
                            value = totalTime,
                            subtitle = stringResource(R.string.insights_total_subtitle),
                            icon = Icons.Default.Timer,
                            shape = itemShape(index = 0, count = topSectionCount)
                        )

                        if (taskDistribution.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = itemShape(index = 1, count = topSectionCount),
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
                                            stringResource(R.string.insights_primary_focus),
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

                        PeakHourCard(
                            peakHour = peakHour,
                            shape = itemShape(
                                index = if (taskDistribution.isNotEmpty()) 2 else 1,
                                count = topSectionCount
                            )
                        )
                    }
                }


                item {
                    Heatmap(
                        data = heatmapHistory,
                        focusTimeByDate = focusTimeByDate
                    )
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
                                title = stringResource(R.string.insights_momentum),
                                subtitle = stringResource(R.string.insights_momentum_subtitle)
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
                                title = stringResource(R.string.insights_weekly),
                                subtitle = stringResource(R.string.insights_weekly_subtitle)
                            )

                            Spacer(Modifier.height(MaterialTheme.spacing.m))

                            if (taskDistribution.isEmpty()) {
                                EmptyDistributionPlaceholder(
                                    stringResource(R.string.insights_weekly_empty)
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
                                title = stringResource(R.string.insights_tasks),
                                subtitle = stringResource(R.string.insights_tasks_subtitle)
                            )

                            Spacer(Modifier.height(MaterialTheme.spacing.s))

                            if (taskDistribution.isEmpty()) {
                                EmptyDistributionPlaceholder(
                                    stringResource(R.string.insights_tasks_empty)
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
}