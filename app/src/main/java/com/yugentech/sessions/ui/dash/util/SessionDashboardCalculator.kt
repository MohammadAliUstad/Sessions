package com.yugentech.sessions.ui.dash.util

import com.yugentech.sessions.localization.UiText
import com.yugentech.sessions.R
import androidx.annotation.StringRes
import com.yugentech.sessions.timer.state.TimerMode
import com.yugentech.sessions.timer.state.TimerState
import com.yugentech.sessions.ui.dash.state.ItemStatus
import com.yugentech.sessions.ui.dash.state.SessionDashboardState
import com.yugentech.sessions.ui.dash.state.SessionVisualItem

/**
 * Works out the session card's schedule and messages from the timer. The messages are [UiText],
 * so they're put into words (in the app's language) only where they're shown: on the card and in
 * the session notification.
 */
object SessionDashboardCalculator {

    private fun text(@StringRes id: Int): UiText = UiText.Res(id)

    private val FocusTaglines = listOf(
        R.string.tagline_focus_zone,
        R.string.tagline_focus_consistency,
        R.string.tagline_focus_one_step,
        R.string.tagline_focus_process,
        R.string.tagline_focus_small_wins,
        R.string.tagline_focus_momentum,
        R.string.tagline_focus_disciplined,
        R.string.tagline_focus_trust,
        R.string.tagline_focus_got_this,
        R.string.tagline_focus_prize,
        R.string.tagline_focus_deep_work,
        R.string.tagline_focus_distractions
    )

    private val BreakTaglines = listOf(
        R.string.tagline_break_reset,
        R.string.tagline_break_breathe,
        R.string.tagline_break_recharge,
        R.string.tagline_break_stretch,
        R.string.tagline_break_hydrate,
        R.string.tagline_break_mindful,
        R.string.tagline_break_step_away,
        R.string.tagline_break_silence,
        R.string.tagline_break_eyes,
        R.string.tagline_break_disconnect
    )

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
                val isLongBreakInterval = config.longBreakEnabled && (i % setsPerLongBreak == 0)

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

        val (mainMsg, subMsg) = when {
            isLongBreakMode -> text(R.string.card_long_break_main) to text(R.string.card_long_break_sub)

            targetSets == 1 -> {
                if (config.focusDuration >= 60)
                    text(R.string.card_power_hour_main) to text(R.string.card_power_hour_sub)
                else
                    text(R.string.card_one_and_done_main) to text(R.string.card_one_and_done_sub)
            }

            targetSets == 2 -> {
                when (setsLeft) {
                    2 -> text(R.string.card_double_header_main) to text(R.string.card_double_header_sub)
                    1 -> text(R.string.card_final_push_main) to text(R.string.card_final_push_finish_strong)
                    else -> text(R.string.card_all_done_main) to text(R.string.card_all_done_sub)
                }
            }

            else -> {
                val random = java.util.Random(completedSets.toLong())
                val tagline = if (isBreakMode) {
                    BreakTaglines[random.nextInt(BreakTaglines.size)]
                } else {
                    FocusTaglines[random.nextInt(FocusTaglines.size)]
                }

                when (setsLeft) {
                    0 -> text(R.string.card_finish_line_main) to text(R.string.card_finish_line_sub)
                    1 -> text(R.string.card_final_push_main) to text(R.string.card_final_push_stay_focused)
                    else -> {
                        if (isBreakMode) {
                            val breakType = if (isLongBreakMode) R.string.mode_long_break else R.string.mode_short_break
                            text(breakType) to text(tagline)
                        } else {
                            UiText.Plural(R.plurals.card_sets_to_go, setsLeft, setsLeft) to text(tagline)
                        }
                    }
                }
            }
        }

        val display = if (isLongBreakMode) text(R.string.card_chill) else UiText.Raw("$currentSet")

        return SessionDashboardState(
            showLongBreakBadge = config.longBreakEnabled && config.setsPerLongBreak < config.targetSets,
            badgeText = UiText.Res(R.string.card_long_break_badge, config.longBreakDuration),
            mainMessage = mainMsg,
            subMessage = subMsg,
            progressDisplay = display,
            isLongBreakActive = isLongBreakMode,
            visualSchedule = visualItems
        )
    }
}