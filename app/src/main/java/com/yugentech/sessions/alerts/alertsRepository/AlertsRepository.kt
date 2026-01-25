package com.yugentech.sessions.alerts.alertsRepository

import android.view.View
import com.yugentech.sessions.alerts.models.AlertsConfiguration
import kotlinx.coroutines.flow.StateFlow

// Interface defining the contract for handling sounds and vibrations
interface AlertsRepository {
    val alertConfiguration: StateFlow<AlertsConfiguration>

    // Trigger actions for various timer lifecycle events
    fun onFocusStart(view: View? = null)
    fun onFocusPause(view: View? = null)
    fun onBreakStart(view: View? = null)
    fun onLeave(view: View? = null)
    fun onFocusStop(view: View? = null)

    // Play a preview of a specific sound file
    fun playPreview(soundId: String?)

    // Trigger a single haptic feedback vibration
    fun performHaptic(view: View? = null)

    // Update settings for sounds and haptics
    suspend fun setSoundEnabled(enabled: Boolean)
    suspend fun setHapticsEnabled(enabled: Boolean)
}