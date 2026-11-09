package com.yugentech.sessions.ui.config.whatsNewScreen

import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Gradient
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.SwipeLeft
import androidx.compose.material.icons.filled.Sync
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.mainScreen.components.itemShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsNewScreen(
    onNavigateBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val layoutDirection = LocalLayoutDirection.current

    val v6Updates = listOf(
        UpdateItem(
            stringResource(R.string.whats_new_v6_13_title),
            stringResource(R.string.whats_new_v6_13_body),
            Icons.Default.Translate
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v6_1_title),
            stringResource(R.string.whats_new_v6_1_body),
            Icons.Default.Gradient
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v6_2_title),
            stringResource(R.string.whats_new_v6_2_body),
            Icons.AutoMirrored.Filled.VolumeUp
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v6_3_title),
            stringResource(R.string.whats_new_v6_3_body),
            Icons.Default.Tune
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v6_4_title),
            stringResource(R.string.whats_new_v6_4_body),
            Icons.Default.AccountCircle
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v6_5_title),
            stringResource(R.string.whats_new_v6_5_body),
            Icons.Default.SpaceDashboard
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v6_6_title),
            stringResource(R.string.whats_new_v6_6_body),
            Icons.Default.Timer
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v6_7_title),
            stringResource(R.string.whats_new_v6_7_body),
            Icons.Default.Gradient
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v6_8_title),
            stringResource(R.string.whats_new_v6_8_body),
            Icons.Default.Explore
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v6_9_title),
            stringResource(R.string.whats_new_v6_9_body),
            Icons.Default.Animation
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v6_10_title),
            stringResource(R.string.whats_new_v6_10_body),
            Icons.AutoMirrored.Filled.Login
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v6_11_title),
            stringResource(R.string.whats_new_v6_11_body),
            Icons.Default.Palette
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v6_12_title),
            stringResource(R.string.whats_new_v6_12_body),
            Icons.Default.Contrast
        )
    )

    val v51Updates = listOf(
        UpdateItem(
            stringResource(R.string.whats_new_v51_1_title),
            stringResource(R.string.whats_new_v51_1_body),
            Icons.Default.Sync
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v51_2_title),
            stringResource(R.string.whats_new_v51_2_body),
            Icons.AutoMirrored.Filled.Login
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v51_3_title),
            stringResource(R.string.whats_new_v51_3_body),
            Icons.Default.AccountCircle
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v51_4_title),
            stringResource(R.string.whats_new_v51_4_body),
            Icons.Default.Celebration
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v51_5_title),
            stringResource(R.string.whats_new_v51_5_body),
            Icons.Default.NotificationsActive
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v51_6_title),
            stringResource(R.string.whats_new_v51_6_body),
            Icons.AutoMirrored.Filled.ShowChart
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v51_7_title),
            stringResource(R.string.whats_new_v51_7_body),
            Icons.Default.AutoAwesome
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v51_8_title),
            stringResource(R.string.whats_new_v51_8_body),
            Icons.Default.Apps
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v51_9_title),
            stringResource(R.string.whats_new_v51_9_body),
            Icons.AutoMirrored.Filled.VolumeUp
        )
    )

    val v5Updates = listOf(
        UpdateItem(
            stringResource(R.string.whats_new_v5_1_title),
            stringResource(R.string.whats_new_v5_1_body),
            Icons.Default.Bookmark
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v5_2_title),
            stringResource(R.string.whats_new_v5_2_body),
            Icons.AutoMirrored.Filled.VolumeUp
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v5_3_title),
            stringResource(R.string.whats_new_v5_3_body),
            Icons.Default.CheckCircle
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v5_4_title),
            stringResource(R.string.whats_new_v5_4_body),
            Icons.AutoMirrored.Filled.ShowChart
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v5_5_title),
            stringResource(R.string.whats_new_v5_5_body),
            Icons.Default.Tune
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v5_6_title),
            stringResource(R.string.whats_new_v5_6_body),
            Icons.Default.Tune
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v5_7_title),
            stringResource(R.string.whats_new_v5_7_body),
            Icons.Default.SwipeLeft
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v5_8_title),
            stringResource(R.string.whats_new_v5_8_body),
            Icons.Default.EmojiEvents
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v5_9_title),
            stringResource(R.string.whats_new_v5_9_body),
            Icons.AutoMirrored.Filled.VolumeUp
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v5_10_title),
            stringResource(R.string.whats_new_v5_10_body),
            Icons.Default.Flag
        )
    )

    val v4Updates = listOf(
        UpdateItem(
            stringResource(R.string.whats_new_v4_1_title),
            stringResource(R.string.whats_new_v4_1_body),
            Icons.Default.NotificationsActive
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v4_2_title),
            stringResource(R.string.whats_new_v4_2_body),
            Icons.Default.AutoAwesome
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v4_3_title),
            stringResource(R.string.whats_new_v4_3_body),
            Icons.Default.SmartDisplay
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v4_4_title),
            stringResource(R.string.whats_new_v4_4_body),
            Icons.Default.Celebration
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v4_5_title),
            stringResource(R.string.whats_new_v4_5_body),
            Icons.Default.NotificationsActive
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v4_6_title),
            stringResource(R.string.whats_new_v4_6_body),
            Icons.Default.History
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v4_7_title),
            stringResource(R.string.whats_new_v4_7_body),
            Icons.AutoMirrored.Filled.VolumeUp
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v4_8_title),
            stringResource(R.string.whats_new_v4_8_body),
            Icons.Default.Celebration
        ),
        UpdateItem(
            stringResource(R.string.whats_new_v4_9_title),
            stringResource(R.string.whats_new_v4_9_body),
            Icons.Default.Apps
        )
    )

    var v55Expanded by rememberSaveable { mutableStateOf(false) }
    var v5Expanded by rememberSaveable { mutableStateOf(false) }
    var v4Expanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.whats_new_title))
                        Text(
                            text = stringResource(R.string.whats_new_current_version, "6.0.0"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { scaffoldPadding ->
        val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = scaffoldPadding.calculateTopPadding()),
            contentPadding = PaddingValues(
                bottom = navBarPadding.calculateBottomPadding(),
                start = MaterialTheme.spacing.m + scaffoldPadding.calculateStartPadding(layoutDirection),
                end = MaterialTheme.spacing.m + scaffoldPadding.calculateEndPadding(layoutDirection)
            ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
        ) {
            itemsIndexed(v6Updates) { index, item ->
                UpdateCard(item, index, v6Updates.size)
            }

            // 5.5.0 collapsible section
            item {
                VersionSectionHeader(
                    version = "5.5.0",
                    isLatest = false,
                    expanded = v55Expanded,
                    onClick = { v55Expanded = !v55Expanded }
                )
            }

            item(key = "v55_collapsible_content") {
                AnimatedVisibility(
                    visible = v55Expanded,
                    enter = fadeIn(tween(AppAnimations.Durations.Standard)) + expandVertically(tween(AppAnimations.Durations.Standard)),
                    exit = fadeOut(tween(AppAnimations.Durations.Standard)) + shrinkVertically(tween(AppAnimations.Durations.Standard))
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
                    ) {
                        v51Updates.forEachIndexed { index, item ->
                            UpdateCard(item, index, v51Updates.size)
                        }
                    }
                }
            }

            // 5.0.0 collapsible section
            item {
                VersionSectionHeader(
                    version = "5.0.0",
                    isLatest = false,
                    expanded = v5Expanded,
                    onClick = { v5Expanded = !v5Expanded }
                )
            }

            item(key = "v5_collapsible_content") {
                AnimatedVisibility(
                    visible = v5Expanded,
                    enter = fadeIn(tween(AppAnimations.Durations.Standard)) + expandVertically(tween(AppAnimations.Durations.Standard)),
                    exit = fadeOut(tween(AppAnimations.Durations.Standard)) + shrinkVertically(tween(AppAnimations.Durations.Standard))
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
                    ) {
                        v5Updates.forEachIndexed { index, item ->
                            UpdateCard(item, index, v5Updates.size)
                        }
                    }
                }
            }

            // 4.0.0 collapsible section
            item {
                VersionSectionHeader(
                    version = "4.0.0",
                    isLatest = false,
                    expanded = v4Expanded,
                    onClick = { v4Expanded = !v4Expanded }
                )
            }

            item(key = "v4_collapsible_content") {
                AnimatedVisibility(
                    visible = v4Expanded,
                    enter = fadeIn(tween(AppAnimations.Durations.Standard)) + expandVertically(tween(AppAnimations.Durations.Standard)),
                    exit = fadeOut(tween(AppAnimations.Durations.Standard)) + shrinkVertically(tween(AppAnimations.Durations.Standard))
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
                    ) {
                        v4Updates.forEachIndexed { index, item ->
                            UpdateCard(item, index, v4Updates.size)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VersionSectionHeader(
    version: String,
    isLatest: Boolean,
    expanded: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "chevron"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.corners.medium))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(
                horizontal = if (onClick != null) MaterialTheme.spacing.s else 0.dp,
                vertical = MaterialTheme.spacing.m
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = stringResource(R.string.whats_new_version, version),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isLatest) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (onClick != null) {
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = stringResource(if (expanded) R.string.collapse else R.string.expand),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.graphicsLayer { rotationZ = chevronRotation }
            )
        }
    }
}

@Composable
private fun UpdateCard(item: UpdateItem, index: Int, totalCount: Int) {
    val shape = itemShape(index, totalCount)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(MaterialTheme.spacing.m)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private data class UpdateItem(
    val title: String,
    val description: String,
    val icon: ImageVector
)
