package com.yugentech.sessions.ui.config.appearanceScreen.components

import androidx.compose.ui.res.stringResource
import com.yugentech.sessions.R
import com.yugentech.sessions.localization.UiText
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.theme.viewmodel.ThemeViewModel
import com.yugentech.sessions.ui.dash.homeScreen.components.durationSelection.SessionProgressCard
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.SessionPulse
import com.yugentech.sessions.ui.dash.mainScreen.components.SectionHeader
import com.yugentech.sessions.ui.dash.state.ItemStatus
import com.yugentech.sessions.ui.dash.state.SessionDashboardState
import com.yugentech.sessions.ui.dash.state.SessionVisualItem
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

/** A session part-way through, so the preview looks like the card mid-focus. */
private val PreviewState = SessionDashboardState(
    showLongBreakBadge = true,
    badgeText = UiText.Res(R.string.card_long_break_badge, 15),
    mainMessage = UiText.Plural(R.plurals.card_sets_to_go, 3, 3),
    subMessage = UiText.Res(R.string.tagline_focus_zone),
    progressDisplay = UiText.Raw("2"),
    visualSchedule = listOf(
        SessionVisualItem.FocusBlock(ItemStatus.Completed),
        SessionVisualItem.ShortBreak(ItemStatus.Completed),
        SessionVisualItem.FocusBlock(ItemStatus.Current),
        SessionVisualItem.ShortBreak(ItemStatus.Upcoming),
        SessionVisualItem.FocusBlock(ItemStatus.Upcoming),
        SessionVisualItem.ShortBreak(ItemStatus.Upcoming),
        SessionVisualItem.FocusBlock(ItemStatus.Upcoming),
        SessionVisualItem.LongBreak(ItemStatus.Upcoming)
    )
)

// How long the preview's pretend focus block takes, so the desert's sun visibly crosses the sky
private const val PREVIEW_BLOCK_SECONDS = 40

/**
 * The Home session card as it will look, in the chosen card style and animation, above the
 * card settings. It plays a pretend focus block that runs and starts again, so effects that
 * follow the timer (the desert's sun) can be seen working.
 */
@Composable
fun SessionCardPreview(
    modifier: Modifier = Modifier,
    viewModel: ThemeViewModel = koinViewModel()
) {
    val themeConfig by viewModel.themeConfiguration.collectAsStateWithLifecycle()
    // Moves on once a second, as the real timer does
    var progress by remember { mutableFloatStateOf(0.15f) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1_000)
            progress = (progress + 1f / PREVIEW_BLOCK_SECONDS).let { if (it > 1f) 0f else it }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            icon = Icons.Default.Visibility,
            title = stringResource(R.string.card_preview_title)
        )
        SessionProgressCard(
            state = PreviewState,
            targetSets = 4,
            isTimerRunning = true,
            onSkipToNext = {},
            cardStyle = themeConfig.cardStyle,
            animationStyle = themeConfig.cardAnimationStyle,
            pulse = SessionPulse(progress = progress, running = true),
            horizontalPadding = 0.dp
        )
    }
}
