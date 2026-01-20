package com.yugentech.sessions.ui.dash.components.homeScreen.durationSelection

import com.yugentech.sessions.timer.states.TimerMode
import com.yugentech.sessions.timer.states.TimerState

object SessionDashboardCalculator {

    fun calculate(state: TimerState): SessionDashboardState {
        // Extract convenient references
        val config = state.timerConfig

        val completedSets = state.completedSets
        val targetSets = config.targetSets
        val currentSet = (completedSets + 1).coerceAtMost(targetSets)
        val setsLeft = (targetSets - completedSets).coerceAtLeast(0)

        // Timer Mode Check
        val isLongBreakMode = state.currentMode == TimerMode.LongBreak
        val isShortBreakMode = state.currentMode == TimerMode.ShortBreak
        val isBreakMode = isLongBreakMode || isShortBreakMode

        // --- 1. Generate Visual Schedule ---
        val visualItems = mutableListOf<SessionVisualItem>()
        // Determine interval: using property from your new TimerConfig
        val setsPerLongBreak = config.setsPerLongBreak

        for (i in 1..targetSets) {
            // A. Add Focus Block
            val setStatus = when {
                i <= completedSets -> ItemStatus.Completed
                i == currentSet && !isBreakMode -> ItemStatus.Current
                else -> ItemStatus.Upcoming
            }
            visualItems.add(SessionVisualItem.FocusBlock(setStatus))

            // B. Add Break (Between sets only)
            if (i < targetSets) {
                // Determine Break Type
                val isLongBreakInterval = (i % setsPerLongBreak == 0)

                // Determine Break Status
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

        // --- 2. Long Break Eligibility (For Badge) ---
        // Rule: Only show badge if a long break actually occurs before the last set
        val setsBeforeLast = (targetSets - 1).coerceAtLeast(0)
        // Note: Updated property name 'focusDuration' (was 'focusDurationMinutes')
        val timeBeforeLastSet = setsBeforeLast * config.focusDuration
        val isEligibleForLongBreak = timeBeforeLastSet >= 100


        // --- 3. Message Logic ---
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