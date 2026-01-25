package com.yugentech.sessions.notifications.notificationRepository

import com.yugentech.sessions.notifications.Notification

// Interface defining the contract for managing both active session alerts and scheduled reminders
interface NotificationRepository {
    // Starts the foreground service to show the active timer in the notification tray
    suspend fun startActiveNotification(notification: Notification)

    // Updates the existing foreground notification with new data like remaining time
    suspend fun updateActiveNotification(notification: Notification)

    // Stops the foreground service and removes the active notification
    suspend fun stopActiveNotification()

    // Schedules a system alarm to trigger a notification at a specific time
    suspend fun scheduleReminder(message: String, hour: Int, minute: Int)

    // Cancels any currently scheduled reminder alarms
    suspend fun cancelReminders()

    // Checks if the app has the necessary system permission to schedule exact alarms
    fun hasExactAlarmPermission(): Boolean
}