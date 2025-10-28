package com.yugentech.sessions.dependencyInjection

class AppInitializer(
    private val notificationInitializer: NotificationInitializer,
) {
    fun initializeAll() {
        notificationInitializer.initialize()
    }
}