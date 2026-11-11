package com.yugentech.sessions.notification.reminders

import com.yugentech.sessions.R
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.yugentech.sessions.notification.datastore.NotificationDataStore
import com.yugentech.sessions.notification.model.NotificationConfig
import com.yugentech.sessions.sessions.repository.SessionsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.random.Random

/**
 * The reminders, set up the same way in Quill and Sessions; only the wording differs.
 *
 * - The daily reminder: an alarm every day at the time the user picks, plainly worded, in the
 *   normal notification channel.
 * - Nudges, in their own quiet channel, sent by [NudgeWorker], which runs every hour. They go by
 *   the days since the user's last session ("days away"):
 *   - Playful: [PLAYFUL_CHANCE_PERCENT]% of days, while 1 to [PLAYFUL_LAST_DAY] days away.
 *   - Away: from [AWAY_FIRST_DAY] days away, every [AWAY_EVERY_DAYS] days, for as long as it lasts.
 *   At most one a day, between [NUDGE_EARLIEST_HOUR]:00 and [NUDGE_LATEST_HOUR]:59, and never
 *   within [NUDGE_GAP_HOURS] hours of the daily reminder. Nothing on a day they've focused.
 *
 * The wording never claims anything the app can't know and never mentions streaks or guilt-trips.
 */
class ReminderManager(
    private val context: Context,
    private val scheduler: NotificationScheduler,
    private val notificationDataStore: NotificationDataStore,
    private val sessionsRepository: SessionsRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun canScheduleExactAlarms(): Boolean = scheduler.canScheduleExactAlarms()

    /**
     * Called from the Application at every start: books the reminders from the saved settings,
     * in case the daily alarm was lost, and clears what the earlier version left behind. Safe at
     * any start, because nothing here depends on when the app was opened.
     */
    fun onAppStart() {
        scope.launch {
            runCatching { apply(currentConfig()) }
                .onFailure { Timber.e(it, "Couldn't book reminders") }
        }
    }

    /** Books or cancels every reminder to match [config]. */
    fun apply(config: NotificationConfig) {
        cancelOldReminders()
        if (config.notificationsEnabled && config.dailyReminderEnabled) {
            scheduleDailyReminder(config.reminderHour, config.reminderMinute)
        } else {
            cancelDailyReminder()
        }
        if (config.notificationsEnabled && config.playfulRemindersEnabled) {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                NUDGE_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<NudgeWorker>(1, TimeUnit.HOURS).build()
            )
        } else {
            WorkManager.getInstance(context).cancelUniqueWork(NUDGE_WORK_NAME)
        }
    }

    fun scheduleDailyReminder(hour: Int, minute: Int) {
        Timber.d("Daily reminder at $hour:${minute.toString().padStart(2, '0')}")
        scheduler.scheduleDailyReminder(
            id = DAILY_REMINDER_ID,
            hour = hour,
            minute = minute,
            title = APP_NAME,
            message = context.getString(dailyMessages.random())
        )
    }

    fun cancelDailyReminder() = scheduler.cancelDailyReminder(DAILY_REMINDER_ID)

    /** Cancels everything, for signing out. */
    fun cancelAll() {
        cancelDailyReminder()
        WorkManager.getInstance(context).cancelUniqueWork(NUDGE_WORK_NAME)
    }

    /**
     * NudgeWorker's hourly check. The first run each day plans the day: the hour from which a
     * nudge may go out, and whether a playful one is rolled. The first run from that hour sends
     * whatever is due, and then nothing more is sent until tomorrow.
     */
    suspend fun sendNudgeIfDue() {
        val config = currentConfig()
        if (!config.notificationsEnabled || !config.playfulRemindersEnabled) return

        val now = LocalDateTime.now()
        val today = now.toLocalDate()
        val dailyReminderHour = config.reminderHour.takeIf { config.dailyReminderEnabled }
        val plan = ReminderStore.dayPlan(context)?.takeIf { it.epochDay == today.toEpochDay() }
            ?: ReminderStore.DayPlan(
                epochDay = today.toEpochDay(),
                sendFromHour = nudgeHour(dailyReminderHour),
                playful = Random.nextInt(100) < PLAYFUL_CHANCE_PERCENT,
                done = false
            ).also { ReminderStore.saveDayPlan(context, it) }

        val hour = now.hour
        if (plan.done || hour < plan.sendFromHour || hour > NUDGE_LATEST_HOUR) return
        if (dailyReminderHour != null && abs(hour - dailyReminderHour) < NUDGE_GAP_HOURS) return

        val lastActive = Instant.ofEpochMilli(lastActivityTime()).atZone(ZoneId.systemDefault()).toLocalDate()
        val daysAway = ChronoUnit.DAYS.between(lastActive, today)
        val messages = nudgeMessages()
        val message = when {
            daysAway >= AWAY_FIRST_DAY ->
                if ((daysAway - AWAY_FIRST_DAY) % AWAY_EVERY_DAYS == 0L) messages.away(daysAway.toInt()) else null
            daysAway in 1..PLAYFUL_LAST_DAY ->
                if (plan.playful) messages.playful() else null
            else -> null
        }

        ReminderStore.saveDayPlan(context, plan.copy(done = true))
        if (message != null) {
            Timber.d("Nudge after $daysAway days away")
            ReminderReceiver.showReminder(context, NUDGE_NOTIFICATION_ID, APP_NAME, message, nudge = true)
        }
    }

    private suspend fun currentConfig(): NotificationConfig =
        notificationDataStore.notificationConfigFlow.first()

    /**
     * What the earlier version booked, on phones that updated: the smart-reminder job and the
     * daily alarm aimed at the old ScheduledReceiver.
     */
    private fun cancelOldReminders() {
        WorkManager.getInstance(context).cancelUniqueWork(OLD_NUDGE_WORK_NAME)
        val oldDaily = PendingIntent.getBroadcast(
            context,
            OLD_DAILY_REQUEST_CODE,
            Intent().setClassName(context.packageName, OLD_RECEIVER_CLASS),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (oldDaily != null) {
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(oldDaily)
            oldDaily.cancel()
        }
    }

    /**
     * An hour between late morning and early evening, so a nudge never wakes anyone, and at
     * least [NUDGE_GAP_HOURS] from the daily reminder's [dailyReminderHour] if there is one.
     */
    private fun nudgeHour(dailyReminderHour: Int?): Int {
        val hours = (NUDGE_EARLIEST_HOUR..NUDGE_LATEST_HOUR).filter { hour ->
            dailyReminderHour == null || abs(hour - dailyReminderHour) >= NUDGE_GAP_HOURS
        }
        return hours.randomOrNull() ?: NUDGE_EARLIEST_HOUR
    }

    /** When the app was installed: "days away" counts from here until the first session. */
    private fun installTime(): Long =
        context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime

    /** The nudges' wording, picked when one is sent: the part that differs between the apps. */
    private class NudgeMessages(val playful: () -> String, val away: (daysAway: Int) -> String)

    /** When the user's last session was. */
    private suspend fun lastActivityTime(): Long =
        sessions().maxOfOrNull { it.timestamp } ?: installTime()

    /** Names one of the user's own tasks, or "your focus session" when they haven't named any. */
    private suspend fun nudgeMessages(): NudgeMessages {
        val tasks = sessions()
            .map { it.sessionTask.trim() }
            .filter { it.isNotBlank() && it != DEFAULT_TASK_NAME }
            .distinct()
        return NudgeMessages(
            playful = {
                val task = tasks.randomOrNull() ?: context.getString(R.string.reminder_default_task)
                context.getString(playfulMessages.random(), task)
            },
            away = { daysAway -> context.resources.getQuantityString(awayMessages.random(), daysAway, daysAway) }
        )
    }

    /** Nothing while no one is signed in. */
    private suspend fun sessions() = sessionsRepository.getSessionsFlow().firstOrNull() ?: emptyList()

    private companion object {
        const val APP_NAME = "Sessions"

        const val DAILY_REMINDER_ID = 2000
        const val NUDGE_NOTIFICATION_ID = 3000
        const val NUDGE_WORK_NAME = "nudges"

        const val OLD_NUDGE_WORK_NAME = "smart_reminder_work"
        const val OLD_DAILY_REQUEST_CODE = 1000
        const val OLD_RECEIVER_CLASS = "com.yugentech.sessions.notification.reminders.ScheduledReceiver"

        /** What a session is called when the user didn't name its task. */
        const val DEFAULT_TASK_NAME = "Focus Session"

        const val PLAYFUL_LAST_DAY = 5
        const val PLAYFUL_CHANCE_PERCENT = 50
        const val AWAY_FIRST_DAY = 6
        const val AWAY_EVERY_DAYS = 2
        const val NUDGE_EARLIEST_HOUR = 11
        const val NUDGE_LATEST_HOUR = 19
        const val NUDGE_GAP_HOURS = 3

        /** The daily reminder: calm and plain. */
        val dailyMessages = listOf(
            R.string.reminder_daily_1,
            R.string.reminder_daily_2,
            R.string.reminder_daily_3,
            R.string.reminder_daily_4
        )

        /** Now and then in the first days: light and playful. Each takes the task's name. */
        val playfulMessages = listOf(
            R.string.reminder_playful_1,
            R.string.reminder_playful_2,
            R.string.reminder_playful_3,
            R.string.reminder_playful_4,
            R.string.reminder_playful_5,
            R.string.reminder_playful_6,
            R.string.reminder_playful_7,
            R.string.reminder_playful_8
        )

        /** After almost a week away, every other day: warm, no pressure. Each takes the days away. */
        val awayMessages = listOf(
            R.plurals.reminder_away_1,
            R.plurals.reminder_away_2,
            R.plurals.reminder_away_3,
            R.plurals.reminder_away_4
        )
    }
}
