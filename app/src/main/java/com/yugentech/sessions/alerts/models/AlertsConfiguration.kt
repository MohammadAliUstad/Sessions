package com.yugentech.sessions.alerts.models

// Data model holding user preferences for sound, haptics, and background ambience
data class AlertsConfiguration(
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val backgroundSound: BackgroundSound = BackgroundSound.NONE
)