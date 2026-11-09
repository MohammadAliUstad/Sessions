package com.yugentech.sessions.ui.dash.state

import com.yugentech.sessions.localization.UiText

data class SessionDashboardState(
    val showLongBreakBadge: Boolean = false,
    val badgeText: UiText = UiText.Empty,
    val mainMessage: UiText = UiText.Empty,
    val subMessage: UiText = UiText.Empty,
    val progressDisplay: UiText = UiText.Empty,
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