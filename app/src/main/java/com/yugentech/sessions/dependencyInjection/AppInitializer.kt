package com.yugentech.sessions.dependencyInjection

class AppInitializer(
    private val notificationInitializer: NotificationInitializer,
    private val workManagerInitializer: WorkManagerInitializer
) {
    fun initializeAll() {
        workManagerInitializer.initialize()
        notificationInitializer.initialize()
    }
}