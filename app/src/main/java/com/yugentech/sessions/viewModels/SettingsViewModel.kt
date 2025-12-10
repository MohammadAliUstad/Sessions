package com.yugentech.sessions.viewModels

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.alerts.alertsDatastore.AlertsConfiguration
import com.yugentech.sessions.alerts.alertsDatastore.AlertsManager
import com.yugentech.sessions.alerts.alertsDatastore.AlertsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingsViewModel(
    private val alertsRepository: AlertsRepository,
    private val alertsManager: AlertsManager
) : ViewModel() {

    // Hot flow of alert settings, defaulting to standard configuration
    val alertConfig: StateFlow<AlertsConfiguration> = alertsManager.alertConfiguration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AlertsConfiguration())

    // Toggles the global sound setting
    fun setSoundEnabled(enabled: Boolean) {
        Timber.d("User toggled sound: $enabled")
        viewModelScope.launch {
            alertsManager.setSoundEnabled(enabled)
        }
    }

    // Toggles the global haptics setting
    fun setHapticsEnabled(enabled: Boolean) {
        Timber.d("User toggled haptics: $enabled")
        viewModelScope.launch {
            alertsManager.setHapticsEnabled(enabled)
        }
    }

    fun performHaptic(view: View? = null) {
        viewModelScope.launch {
            alertsRepository.performHaptic(view)
        }
    }
}