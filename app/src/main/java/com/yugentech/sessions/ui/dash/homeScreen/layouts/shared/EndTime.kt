package com.yugentech.sessions.ui.dash.homeScreen.layouts.shared

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Date
import kotlin.math.abs

/**
 * The clock time a running session ends, [secondsLeft] from now, in the phone's own 12 or 24
 * hour format.
 *
 * [secondsLeft] is rounded down, so each tick's estimate lands up to a second away from the real
 * end. When that end sits near a minute boundary, fresh estimates fall either side of it and the
 * shown minute flips back and forth. So the end is worked out once and kept until the estimate
 * really moves (a pause, a skip, a new session).
 */
@Composable
internal fun sessionEndTime(secondsLeft: Int): String {
    val context = LocalContext.current
    val anchor = remember { EndTimeAnchor() }
    val estimate = System.currentTimeMillis() + secondsLeft * 1000L
    if (abs(estimate - anchor.millis) > END_TIME_TOLERANCE_MS) anchor.millis = estimate
    return DateFormat.getTimeFormat(context).format(Date(anchor.millis))
}

/** Holds the end time between ticks; not state, as each tick already recomposes the timer. */
private class EndTimeAnchor { var millis = 0L }

/** More than the rounding wobble, less than any real change in when the session ends. */
private const val END_TIME_TOLERANCE_MS = 2_000L
