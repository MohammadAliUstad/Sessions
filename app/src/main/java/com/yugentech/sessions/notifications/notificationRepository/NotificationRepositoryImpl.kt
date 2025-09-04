package com.yugentech.sessions.notifications.notificationRepository

import com.yugentech.sessions.notifications.NotificationService
import com.yugentech.sessions.notifications.utils.NotificationType
import com.yugentech.sessions.notifications.utils.Notification

class NotificationRepositoryImpl(
    private val notificationService: NotificationService
) : NotificationRepository {

    override fun setupNotifications() {
        notificationService.createNotificationChannels()
    }

    override fun showActiveSessionNotification(
        message: String,
        timeRemainingMinutes: Int,
        totalMinutes: Int
    ) {
        val notification = Notification(
            id = NotificationService.ACTIVE_NOTIFICATION_ID,
            type = NotificationType.ACTIVE_SESSION,
            title = "Session Active",
            message = message,
            isOngoing = true,
            timeRemainingMinutes = timeRemainingMinutes,
            totalMinutes = totalMinutes
        )
        notificationService.showNotification(notification)
    }

    override fun showReminderNotification(message: String) {
        val notification = Notification(
            id = NotificationService.REMINDER_NOTIFICATION_ID,
            type = NotificationType.REMINDER,
            title = "Study Reminder",
            message = message,
            isOngoing = false
        )
        notificationService.showNotification(notification)
    }

    override fun scheduleReminder(hour: Int, minute: Int, message: String): Boolean {
        return notificationService.scheduleReminderNotification(hour, minute, message)
    }

    override fun cancelReminder(hour: Int, minute: Int) {
        notificationService.cancelScheduledReminder(hour, minute)
    }

    override fun hideActiveNotification() {
        notificationService.hideNotification(NotificationService.ACTIVE_NOTIFICATION_ID)
    }

    override fun hideReminderNotification() {
        notificationService.hideNotification(NotificationService.REMINDER_NOTIFICATION_ID)
    }

    override fun hideAllNotifications() {
        notificationService.hideAllNotifications()
    }

    override fun hasNotificationPermission(): Boolean {
        return notificationService.hasNotificationPermission()
    }

    override fun canScheduleExactAlarms(): Boolean {
        return notificationService.canScheduleExactAlarms()
    }
}