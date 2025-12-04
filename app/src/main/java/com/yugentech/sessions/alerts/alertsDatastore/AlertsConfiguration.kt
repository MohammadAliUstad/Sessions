package com.yugentech.sessions.alerts.alertsDatastore

// Data model representing the user's audio and haptic preferences
data class AlertsConfiguration(
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true
)