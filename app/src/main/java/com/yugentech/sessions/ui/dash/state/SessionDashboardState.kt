package com.yugentech.sessions.ui.dash.state

import com.yugentech.sessions.utils.AppConstants.EMPTY_STRING

data class SessionDashboardState(
    val showLongBreakBadge: Boolean = false,
    val badgeText: String = EMPTY_STRING,
    val mainMessage: String = EMPTY_STRING,
    val subMessage: String = EMPTY_STRING,
    val progressDisplay: String = EMPTY_STRING,
    val isLongBreakActive: Boolean = false,
    val visualSchedule: List<SessionVisualItem> = emptyList()
)

sealed interface SessionVisualItem {
    val status: ItemStatus

    data class FocusBlock(override val status: ItemStatus) : SessionVisualItem
    data class ShortBreak(override val status: ItemStatus) : SessionVisualItem
    data class LongBreak(override val status: ItemStatus) : SessionVisualItem
}

enum class ItemStatus {
    Completed, Current, Upcoming
}