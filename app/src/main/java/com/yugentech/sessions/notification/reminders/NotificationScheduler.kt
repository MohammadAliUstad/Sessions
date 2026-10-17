package com.yugentech.sessions.notification.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import timber.log.Timber
import java.util.Calendar

/** Books the daily reminder as an alarm, the same way as in Quill. */
class NotificationScheduler(
    private val context: Context
) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * A reminder every day at [hour]:[minute], replacing any earlier one with the same [id].
     * Only the next one is booked: when it rings, ReminderReceiver posts it and books the day
     * after. The time is kept in [ReminderStore] too, so BootReceiver can book it again after a
     * restart, which clears every alarm.
     */
    fun scheduleDailyReminder(id: Int, hour: Int, minute: Int, title: String, message: String) {
        ReminderStore.saveDaily(context, ReminderStore.Daily(id, hour, minute, title, message))
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_ID, id)
            putExtra(ReminderReceiver.EXTRA_TITLE, title)
            putExtra(ReminderReceiver.EXTRA_MESSAGE, message)
        }
        setAlarm(id, nextOccurrence(hour, minute), intent)
    }

    fun cancelDailyReminder(id: Int) {
        ReminderStore.removeDaily(context, id)
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    /**
     * Whether reminders ring at the exact minute. Android 12+ lets the user revoke this; without
     * it reminders still come, just possibly a few minutes late.
     */
    fun canScheduleExactAlarms(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

    /** Exact where allowed, inexact where it is not: a reminder a few minutes late beats none. */
    private fun setAlarm(id: Int, timeInMillis: Long, intent: Intent) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Revoked between the check and the call.
            Timber.w(e, "Exact alarm denied, falling back")
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
        }
    }

    companion object {
        /** The next [hour]:[minute] still ahead: today's, or tomorrow's if that has passed. */
        fun nextOccurrence(hour: Int, minute: Int, now: Long = System.currentTimeMillis()): Long {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = now
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (calendar.timeInMillis <= now) calendar.add(Calendar.DAY_OF_YEAR, 1)
            return calendar.timeInMillis
        }
    }
}
