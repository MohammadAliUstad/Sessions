package com.yugentech.sessions.ui.dash.components.homeScreen.durationSelection

// Add these to HomeUiState.kt

// The container for all dashboard data
data class SessionDashboardState(
    val showLongBreakBadge: Boolean = false,
    val badgeText: String = "",
    val mainMessage: String = "",
    val subMessage: String = "",
    val progressDisplay: String = "",
    val isLongBreakActive: Boolean = false,

    // NEW: The list of visual items to render in the progress bar
    val visualSchedule: List<SessionVisualItem> = emptyList()
)

// The individual items in the progress bar
sealed interface SessionVisualItem {
    val status: ItemStatus

    data class FocusBlock(override val status: ItemStatus) : SessionVisualItem
    data class ShortBreak(override val status: ItemStatus) : SessionVisualItem
    data class LongBreak(override val status: ItemStatus) : SessionVisualItem
}

enum class ItemStatus {
    Completed, Current, Upcoming
}