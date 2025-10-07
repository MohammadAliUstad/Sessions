package com.yugentech.sessions.alerts.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.alerts.model.AlertsConfiguration
import com.yugentech.sessions.alerts.repository.AlertsRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class AlertsViewModel(
    private val alertsRepository: AlertsRepository
) : ViewModel() {

    val alertConfigurations: StateFlow<AlertsConfiguration> = alertsRepository.alertConfiguration

    fun setSoundEnabled(enabled: Boolean) {
        Timber.d("User toggled sound: $enabled")
        viewModelScope.launch {
            alertsRepository.setSoundEnabled(enabled)
        }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        Timber.d("User toggled haptics: $enabled")
        viewModelScope.launch {
            alertsRepository.setHapticsEnabled(enabled)
        }
    }

    fun performHaptic(view: View? = null) {
        alertsRepository.performHaptic(view)
    }
}