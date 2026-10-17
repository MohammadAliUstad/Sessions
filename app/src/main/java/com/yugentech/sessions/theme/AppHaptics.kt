package com.yugentech.sessions.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

// Passes haptics through to the system only while the "Haptic Feedback" setting is on.
private class GatedHapticFeedback(
    private val system: HapticFeedback,
    private val enabled: () -> Boolean
) : HapticFeedback {
    override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
        if (enabled()) system.performHapticFeedback(hapticFeedbackType)
    }
}

// SessionsTheme provides this as LocalHapticFeedback, so every haptic in the UI (ours and the
// framework's, such as text selection handles) follows the setting. The system's own touch
// feedback setting still applies underneath. Timer alerts (start, stop, goal reached) don't go
// through here: they fire from the timer service via AlertsRepository and HapticService.
@Composable
internal fun rememberGatedHaptics(enabled: Boolean): HapticFeedback {
    val system = LocalHapticFeedback.current
    val currentEnabled by rememberUpdatedState(enabled)
    return remember(system) { GatedHapticFeedback(system) { currentEnabled } }
}
