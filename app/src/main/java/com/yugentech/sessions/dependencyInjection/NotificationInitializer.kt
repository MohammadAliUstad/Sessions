package com.yugentech.sessions.dependencyInjection

import com.yugentech.sessions.notifications.active.ActiveService
import kotlinx.coroutines.runBlocking

class NotificationInitializer(
    private val prefs: NotificationPrefsDataStore,
    private val activeService: ActiveService
) {
    fun initialize() = runBlocking {
        if (!prefs.areChannelsCreated()) {
            activeService.createNotificationChannels()
            prefs.setChannelsCreated(true)
        }
    }
}