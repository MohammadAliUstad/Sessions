package com.yugentech.sessions.ui.config.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InsightsScreen(
    totalTime: String,
    streakCount: Int,
    taskDistribution: Map<String, Int>,
    dailyVolume: Map<Int, Int>, // Weekly rhythm (1-7)
    heatmapHistory: Map<LocalDate, Int>, // Full history for Heatmap
    peakHour: Int,
    onBack: () -> Unit
) {
    val tokens = MaterialTheme.spacing
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
            contentPadding = PaddingValues(tokens.m),
            verticalArrangement = Arrangement.spacedBy(tokens.m)
        ) {
            // 1. Hero Metric
            item {
                InsightMetricCard(
                    title = "Total Focus Time",
                    value = totalTime,
                    subtitle = "Cumulative time across all sessions",
                    icon = Icons.Default.Timer
                )
            }

            // 2. Primary Focus
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
                            modifier = Modifier.padding(tokens.m),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(Modifier.width(tokens.m))
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

            // 3. Peak Performance
            if (peakHour != -1) {
                item {
                    PeakHourCard(peakHour = peakHour)
                }
            }

            // 4. Calendar Heatmap (Expressive Grit Style)
            item {
                Material3Heatmap(data = heatmapHistory)
            }

            // 5. Momentum (Consistency Text)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(MaterialTheme.corners.large),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(Modifier.padding(tokens.m)) {
                        InsightSectionHeader("Momentum", "Your consistency streak")
                        Spacer(Modifier.height(tokens.s))
                        ConsistencyCard(streakCount = streakCount)
                    }
                }
            }

            // 6. Weekly Rhythm
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(MaterialTheme.corners.large)
                ) {
                    Column(Modifier.padding(tokens.m)) {
                        InsightSectionHeader("Weekly Rhythm", "Session volume by day of week")
                        Spacer(Modifier.height(tokens.m))
                        WeeklyRhythmChart(dailyVolume = dailyVolume)
                    }
                }
            }

            // 7. Project Allocation
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(MaterialTheme.corners.large)
                ) {
                    Column(Modifier.padding(tokens.m)) {
                        InsightSectionHeader("Task Allocation", "How your time is distributed")
                        Spacer(Modifier.height(tokens.s))
                        if (taskDistribution.isEmpty()) {
                            EmptyDistributionPlaceholder()
                        } else {
                            TaskDistributionList(taskDistribution = taskDistribution)
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
//  HEATMAP COMPONENTS
// ============================================================================
@Composable
fun Material3Heatmap(
    data: Map<LocalDate, Int>,
    modifier: Modifier = Modifier
) {
    // Generate full year data based on input
    val heatmapWeeks = remember(data) { generateHeatmapData(data) }
    val tokens = MaterialTheme.spacing
    val listState = rememberLazyListState()

    // Scroll to the end (Today) initially
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
        Column(Modifier.padding(tokens.m)) {
            // Header
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

                // Legend
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Less", style = MaterialTheme.typography.labelSmall)
                    HeatmapCell(1) // Light
                    HeatmapCell(3) // Medium
                    HeatmapCell(5) // Heavy
                    Text("More", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(Modifier.height(tokens.m))

            Row {
                // Fixed Day Labels (Left Side)
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 24.dp) // Push down to account for month labels
                ) {
                    listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.size(height = 20.dp, width = 16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(Modifier.width(tokens.s))

                // The Scrollable Grid
                LazyRow(
                    state = listState,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    // Removed reverseLayout for simpler Month alignment logic
                    // We simply scroll to end on load
                ) {
                    items(heatmapWeeks) { week ->
                        Column {
                            // Month Label Slot
                            Box(modifier = Modifier.height(20.dp)) {
                                if (week.firstDayOfMonth != null) {
                                    Text(
                                        text = week.firstDayOfMonth,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        // Prevent clipping if label is long
                                        softWrap = false
                                    )
                                }
                            }

                            Spacer(Modifier.height(4.dp))

                            // The 7 Days
                            HeatmapWeekColumn(week.days)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeatmapWeekColumn(days: List<HeatmapDay>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(7) { dayIndex ->
            // Find the day (Mon=1 ... Sun=7)
            val day = days.find { it.date.dayOfWeek.value == dayIndex + 1 }

            if (day != null) {
                HeatmapCell(
                    intensity = day.intensity,
                    dayOfMonth = day.date.dayOfMonth // Pass the number here
                )
            } else {
                HeatmapCell(intensity = -1)
            }
        }
    }
}

@Composable
private fun HeatmapCell(
    intensity: Int,
    dayOfMonth: Int? = null // Optional, so legend cells work too
) {
    // Determine background color
    val backgroundColor = when {
        intensity == -1 -> Color.Transparent
        intensity == 0 -> MaterialTheme.colorScheme.surfaceContainerHighest
        intensity < 2 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
        intensity < 4 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
        else -> MaterialTheme.colorScheme.primary
    }

    // Determine text color for contrast
    val textColor = when {
        intensity == -1 -> Color.Transparent
        intensity == 0 -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        intensity < 2 -> MaterialTheme.colorScheme.primary // Dark text on light bg
        intensity < 4 -> MaterialTheme.colorScheme.onPrimaryContainer // Readable on mid-tone
        else -> MaterialTheme.colorScheme.onPrimary // Light text on dark/solid bg
    }

    Box(
        modifier = Modifier
            .size(24.dp) // Increased slightly from 20dp to fit text better
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (dayOfMonth != null && intensity != -1) {
            Text(
                text = dayOfMonth.toString(),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp, // Explicitly small to fit in the box
                color = textColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Data Helper
// Data Helper classes
data class HeatmapDay(val date: LocalDate, val intensity: Int)
data class HeatmapWeek(val weekIndex: Int, val days: List<HeatmapDay>, val firstDayOfMonth: String?)

fun generateHeatmapData(
    activeDates: Map<LocalDate, Int>
): List<HeatmapWeek> {
    val endDate = LocalDate.now()
    // Show last 52 weeks (1 Year)
    val startDate = endDate.minusWeeks(52).minusDays(endDate.dayOfWeek.value.toLong() - 1)

    val weeks = mutableListOf<HeatmapWeek>()
    val daysBetween = ChronoUnit.DAYS.between(startDate, endDate)

    var currentWeekIndex = 0
    var currentWeekDays = mutableListOf<HeatmapDay>()

    for (i in 0..daysBetween) {
        val date = startDate.plusDays(i)
        val intensity = activeDates[date] ?: 0
        currentWeekDays.add(HeatmapDay(date, intensity))

        // If we filled a week or it's the last day
        if (currentWeekDays.size == 7 || i == daysBetween) {

            // FIX: Only label the week that actually contains the 1st of the month
            val firstDayOfMonthLabel = currentWeekDays.firstOrNull { it.date.dayOfMonth == 1 }
                ?.date?.format(DateTimeFormatter.ofPattern("MMM"))

            weeks.add(
                HeatmapWeek(
                    currentWeekIndex,
                    ArrayList(currentWeekDays),
                    firstDayOfMonthLabel
                )
            )

            currentWeekIndex++
            currentWeekDays = mutableListOf()
        }
    }
    return weeks
}

@Composable
fun WeeklyRhythmChart(dailyVolume: Map<Int, Int>) {
    val tokens = MaterialTheme.spacing
    val days = listOf("S", "M", "T", "W", "T", "F", "S")
    // Ensure we don't divide by zero
    val maxVolume = dailyVolume.values.maxOrNull()?.coerceAtLeast(1) ?: 1

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(MaterialTheme.corners.medium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Reduced horizontal padding slightly since SpaceEvenly adds its own gaps
                .padding(vertical = tokens.l, horizontal = tokens.s),
            // FIX: Use SpaceEvenly to ensure equal gaps between bars AND the edges
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEachIndexed { index, day ->
                val count = dailyVolume[index + 1] ?: 0
                val progress = count.toFloat() / maxVolume

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // 1. The Track (Background Pill)
                    Box(
                        modifier = Modifier
                            .height(120.dp)
                            .width(16.dp)
                            .clip(RoundedCornerShape(100))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        // 2. The Fill (Foreground Pill)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(progress)
                                .clip(RoundedCornerShape(100))
                                .background(
                                    if (count == maxVolume && count > 0)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                )
                        )
                    }

                    Spacer(Modifier.height(tokens.s))

                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (count == maxVolume) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun PeakHourCard(peakHour: Int) {
    val tokens = MaterialTheme.spacing

    val period = if (peakHour < 12) "AM" else "PM"
    val displayHour = when {
        peakHour == 0 -> 12
        peakHour > 12 -> peakHour - 12
        else -> peakHour
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MaterialTheme.corners.large),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(tokens.m),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$displayHour$period",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(Modifier.width(tokens.m))
            Column {
                Text(
                    "Peak Productivity",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Your most active hour for focused work",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ConsistencyCard(streakCount: Int) {
    val tokens = MaterialTheme.spacing

    val (title, message) = when {
        streakCount == 0 -> "Getting Started" to "Begin your first session to start building a streak"
        streakCount < 3 -> "Building Momentum" to "You're on a $streakCount-day streak. Keep going!"
        streakCount < 7 -> "Strong Start" to "You've maintained a $streakCount-day focus streak. Great progress!"
        streakCount < 14 -> "Habit Forming" to "Impressive $streakCount-day streak! You're building a solid routine."
        streakCount < 30 -> "Committed" to "$streakCount days of consistent focus. You're on fire!"
        else -> "Focus Master" to "Outstanding $streakCount-day streak. Your dedication is remarkable!"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(MaterialTheme.corners.medium)
    ) {
        Column(Modifier.padding(tokens.m)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(tokens.xs))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun TaskDistributionList(taskDistribution: Map<String, Int>) {
    val totalSeconds = taskDistribution.values.sum().toFloat()
    val tokens = MaterialTheme.spacing

    Column(verticalArrangement = Arrangement.spacedBy(tokens.s)) {
        taskDistribution.toList().sortedByDescending { it.second }.take(5)
            .forEach { (task, duration) ->
                val percentage = (duration / totalSeconds)
                TaskProgressItem(label = task, percentage = percentage)
            }
    }
}

@Composable
fun TaskProgressItem(label: String, percentage: Float) {
    val tokens = MaterialTheme.spacing
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${(percentage * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall
            )
        }
        Spacer(Modifier.height(tokens.xxs))
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(tokens.xs)),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
        )
    }
}

@Composable
fun EmptyDistributionPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                MaterialTheme.colorScheme.surfaceContainerLow,
                RoundedCornerShape(MaterialTheme.corners.medium)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Complete a session to see distribution",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun InsightMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val tokens = MaterialTheme.spacing

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(tokens.m),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.labelMedium)
                Text(
                    value,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun InsightSectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(vertical = MaterialTheme.spacing.s)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            // FIX: Use onSurface for maximum brightness
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            // FIX: Use onSurface with alpha instead of the dimmer onSurfaceVariant
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}