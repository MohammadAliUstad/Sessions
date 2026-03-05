package com.yugentech.sessions.ui.dash.util

import com.yugentech.sessions.timer.state.TimerMode
import com.yugentech.sessions.timer.state.TimerState
import com.yugentech.sessions.ui.dash.state.ItemStatus
import com.yugentech.sessions.ui.dash.state.SessionDashboardState
import com.yugentech.sessions.ui.dash.state.SessionVisualItem

object SessionDashboardCalculator {

    fun calculate(state: TimerState): SessionDashboardState {
        val config = state.timerConfig
        val completedSets = state.completedSets
        val targetSets = config.targetSets
        val currentSet = (completedSets + 1).coerceAtMost(targetSets)
        val setsLeft = (targetSets - completedSets).coerceAtLeast(0)
        val isLongBreakMode = state.currentMode == TimerMode.LongBreak
        val isShortBreakMode = state.currentMode == TimerMode.ShortBreak
        val isBreakMode = isLongBreakMode || isShortBreakMode
        val visualItems = mutableListOf<SessionVisualItem>()
        val setsPerLongBreak = config.setsPerLongBreak

        for (i in 1..targetSets) {
            val setStatus = when {
                i <= completedSets -> ItemStatus.Completed
                i == currentSet && !isBreakMode -> ItemStatus.Current
                else -> ItemStatus.Upcoming
            }
            visualItems.add(SessionVisualItem.FocusBlock(setStatus))

            if (i < targetSets) {
                val isLongBreakInterval = (i % setsPerLongBreak == 0)

                val breakStatus = when {
                    i < completedSets -> ItemStatus.Completed
                    i == completedSets -> if (isBreakMode) ItemStatus.Current else ItemStatus.Completed
                    else -> ItemStatus.Upcoming
                }

                if (isLongBreakInterval) {
                    visualItems.add(SessionVisualItem.LongBreak(breakStatus))
                } else {
                    visualItems.add(SessionVisualItem.ShortBreak(breakStatus))
                }
            }
        }

        val setsBeforeLast = (targetSets - 1).coerceAtLeast(0)
        val timeBeforeLastSet = setsBeforeLast * config.focusDuration
        val isEligibleForLongBreak = timeBeforeLastSet >= 100


        val (mainMsg, subMsg) = when {
            isLongBreakMode -> "You earned this." to "Reset & recover"

            targetSets == 1 -> {
                if (config.focusDuration >= 60)
                    "Power Hour" to "Deep Work Session"
                else
                    "One and done" to "Make it count"
            }

            targetSets == 2 -> {
                when (setsLeft) {
                    2 -> "Double Header" to "Building momentum"
                    1 -> "Final push!" to "Finish strong"
                    else -> "All done!" to "Great session"
                }
            }

            else -> {
                when (setsLeft) {
                    0 -> "Finish line!" to "You made it"
                    1 -> "Final push!" to "Stay focused"
                    else -> "$setsLeft sets to go" to "Keep the rhythm"
                }
            }
        }

        val display = if (isLongBreakMode) "Chill" else "$currentSet"

        return SessionDashboardState(
            showLongBreakBadge = isEligibleForLongBreak,
            badgeText = "${config.longBreakDuration}m Long Break",
            mainMessage = mainMsg,
            subMessage = subMsg,
            progressDisplay = display,
            isLongBreakActive = isLongBreakMode,
            visualSchedule = visualItems
        )
    }
}