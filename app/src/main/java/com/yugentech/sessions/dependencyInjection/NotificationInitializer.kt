package com.yugentech.sessions.dependencyInjection

import com.yugentech.sessions.notifications.active.NotificationService
import kotlinx.coroutines.runBlocking

class NotificationInitializer(
    private val prefs: NotificationPrefsDataStore,
    private val notificationService: NotificationService
) {
    fun initialize() = runBlocking {
        if (!prefs.areChannelsCreated()) {
            notificationService.createNotificationChannels()
            prefs.setChannelsCreated(true)
        }
    }
}