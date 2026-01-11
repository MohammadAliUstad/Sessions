package com.yugentech.sessions.alerts.models

data class AlertsConfiguration(
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val backgroundSound: BackgroundSound = BackgroundSound.NONE
)