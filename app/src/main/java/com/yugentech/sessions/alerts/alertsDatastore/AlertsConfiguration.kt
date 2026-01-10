package com.yugentech.sessions.alerts.alertsDatastore

import com.yugentech.sessions.alerts.alertsDatastore.backgroundSounds.BackgroundSound

data class AlertsConfiguration(
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val backgroundSound: BackgroundSound = BackgroundSound.NONE
)