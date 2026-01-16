package com.yugentech.sessions.alerts.alertsRepository

import android.view.View
import com.yugentech.sessions.alerts.models.AlertsConfiguration
import kotlinx.coroutines.flow.StateFlow

interface AlertsRepository {
    val alertConfiguration: StateFlow<AlertsConfiguration>
    fun onFocusStart(view: View? = null)
    fun onBreakStart(view: View? = null)
    fun onFocusStop(view: View? = null)
    fun playPreview(soundId: String?)
    fun performHaptic(view: View? = null)
    suspend fun setBackgroundSound(soundId: String?)
    suspend fun setSoundEnabled(enabled: Boolean)
    suspend fun setHapticsEnabled(enabled: Boolean)
}