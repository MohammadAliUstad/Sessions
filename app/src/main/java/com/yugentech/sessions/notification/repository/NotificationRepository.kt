package com.yugentech.sessions.notification.repository

// Interface defining the contract for the active session notification. Reminders are booked by
// ReminderManager, the same way as in Quill.
interface NotificationRepository {

    // Starts the foreground service. ActiveForeground reads all timer state directly
    // from TimerRepository — no Notification object needs to be passed in.
    fun startActiveNotification()

    // Stops the foreground service and removes the active notification.
    fun stopActiveNotification()

    // Tells the service to save the session and stop with the "Goal Reached" sound.
    fun finishActiveNotification()
}
