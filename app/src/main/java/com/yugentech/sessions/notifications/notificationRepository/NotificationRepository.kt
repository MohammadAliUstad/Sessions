package com.yugentech.sessions.notifications.notificationRepository

import com.yugentech.sessions.notifications.Notification

// Defines the contract for managing active sessions and scheduled reminders
interface NotificationRepository {
    // Starts the foreground service for an active focus session
    suspend fun startActiveSession(notification: Notification)

    // Updates the notification content (e.g., remaining time) for the active session
    suspend fun updateActiveSession(notification: Notification)

    // Stops the active session foreground service
    suspend fun stopActiveSession()

    // Schedules a precise alarm for daily study reminders
    suspend fun scheduleReminder(message: String, hour: Int, minute: Int)

    // Cancels all pending scheduled reminders
    suspend fun cancelReminders()

    // Checks if the app has permission to schedule exact alarms (Android 12+)
    fun hasExactAlarmPermission(): Boolean
}