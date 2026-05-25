package com.yugentech.sessions.notification.repository

// Interface defining the contract for managing both active session alerts and scheduled reminders
interface NotificationRepository {

    // Starts the foreground service. ActiveForeground reads all timer state directly
    // from TimerRepository — no Notification object needs to be passed in.
    fun startActiveNotification()

    // Stops the foreground service and removes the active notification.
    fun stopActiveNotification()

    // Tells the service to save the session and stop with the "Goal Reached" sound.
    fun finishActiveNotification()

    // Schedules a system alarm to trigger a notification at a specific time.
    suspend fun scheduleReminder(message: String, hour: Int, minute: Int)

    // Cancels any currently scheduled reminder alarms.
    suspend fun cancelReminders()

    // Checks if the app has the necessary system permission to schedule exact alarms.
    fun hasExactAlarmPermission(): Boolean

    // Schedules periodic smart reminders using WorkManager.
    fun scheduleSmartReminders()

    // Cancels scheduled smart reminders.
    fun cancelSmartReminders()
}