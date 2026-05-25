package com.yugentech.sessions.notification.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yugentech.sessions.notification.datastore.NotificationDataStore
import com.yugentech.sessions.notification.model.Notification
import com.yugentech.sessions.notification.model.NotificationType
import com.yugentech.sessions.notification.service.NotificationService
import com.yugentech.sessions.sessions.repository.SessionsRepository
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class SmartReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val notificationDataStore: NotificationDataStore by inject()
    private val sessionsRepository: SessionsRepository by inject()
    private val notificationService: NotificationService by inject()

    override suspend fun doWork(): Result {
        val config = notificationDataStore.notificationConfigFlow.first()
        
        Timber.d("SmartReminderWorker: Running work. Config: $config")

        if (!config.notificationsEnabled || !config.smartRemindersEnabled) {
            Timber.d("SmartReminderWorker: Notifications or SmartReminders disabled. Skipping.")
            return Result.success()
        }

        val sessions = sessionsRepository.getSessionsFlow().first()
        val lastSession = sessions.firstOrNull()
        val currentTime = System.currentTimeMillis()
        
        Timber.d("SmartReminderWorker: Last session: $lastSession, Current time: $currentTime")

        // Check 6-day inactivity
        if (lastSession != null) {
            val millisSinceLast = currentTime - lastSession.timestamp
            val daysSinceLast = TimeUnit.MILLISECONDS.toDays(millisSinceLast)
            Timber.d("SmartReminderWorker: Days since last session: $daysSinceLast")
            
            if (daysSinceLast >= 6) {
                // To avoid spamming (2x a day), we only send a "Come back" notification
                // if it's been an even number of days since the 6-day mark (once every 2 days).
                // Or simply: (daysSinceLast % 2 == 0) ensures it triggers on day 6, 8, 10...
                // And we use a 12-hour window check within that day to ensure it only fires once.
                val hoursSinceLast = TimeUnit.MILLISECONDS.toHours(millisSinceLast)
                val isCorrectDay = daysSinceLast % 2 == 0L
                val isFirstHalfOfDay = (hoursSinceLast % 24) < 12 
                
                if (isCorrectDay && isFirstHalfOfDay) {
                    Timber.d("SmartReminderWorker: Triggering inactivity notification (Once every 2 days logic)")
                    sendInactivityNotification()
                } else {
                    Timber.d("SmartReminderWorker: Skipping inactivity notification to maintain 2-day interval.")
                }
                return Result.success()
            }
        } else {
            Timber.d("SmartReminderWorker: No sessions found.")
        }

        // Random playful reminder (approx 30% chance when worker runs)
        val roll = Random.nextInt(100)
        Timber.d("SmartReminderWorker: Random roll: $roll (threshold 30)")
        if (roll < 30) {
            val taskNames = sessions.map { it.sessionTask }
                .filter { it.isNotBlank() }
                .distinct()

            val selectedTask = if (taskNames.isNotEmpty()) {
                taskNames.random()
            } else {
                "your focus session"
            }

            Timber.d("SmartReminderWorker: Triggering playful notification for $selectedTask")
            sendPlayfulNotification(selectedTask)
        } else {
            Timber.d("SmartReminderWorker: Random roll failed. No playful notification.")
        }

        return Result.success()
    }

    private fun sendInactivityNotification() {
        val messages = listOf(
            "It's been a while! Your focus streak is waiting for you.",
            "We miss seeing you focus. Ready for a quick session?",
            "Don't let your progress slip away. Let's get back to it!",
            "6 days is a long time! How about a 15-minute focus session?"
        )
        showNotification("Come back!", messages.random())
    }

    private fun sendPlayfulNotification(taskName: String) {
        val messages = listOf(
            "Ready to tackle $taskName again? You've got this!",
            "Remember how good it felt to finish $taskName? Let's do it again.",
            "Your future self will thank you for focusing on $taskName today.",
            "Is it time for some $taskName? Just a quick session!",
            "Hey! A little progress on $taskName goes a long way.",
            "Focused minds achieve great things. Ready for $taskName?",
            "Psst... $taskName is calling your name!",
            "One step closer to mastering $taskName. Shall we?",
            "Don't let $taskName be lonely today!",
            "A quick focus session on $taskName? You'll feel great after."
        )
        showNotification("Focus Time!", messages.random())
    }

    private fun showNotification(title: String, message: String) {
        val notification = Notification(
            id = Random.nextInt(1000, 9999),
            type = NotificationType.SCHEDULED,
            title = title,
            message = message,
            isOngoing = false
        )
        notificationService.showNotification(notification)
    }
}
