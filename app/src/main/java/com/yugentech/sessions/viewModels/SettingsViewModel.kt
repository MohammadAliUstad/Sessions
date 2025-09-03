package com.yugentech.sessions.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.alerts.alertsDatastore.AlertsConfiguration
import com.yugentech.sessions.alerts.alertsDatastore.AlertsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val alertsManager: AlertsManager
) : ViewModel() {

    val alertConfig: StateFlow<AlertsConfiguration> = alertsManager.alertConfiguration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AlertsConfiguration())

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            alertsManager.setSoundEnabled(enabled)
        }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            alertsManager.setHapticsEnabled(enabled)
        }
    }
}