package com.yugentech.sessions.notifications.scheduled

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission

class ReminderManager(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun scheduleReminder(
        message: String,
        delayMillis: Long,
        requestCode: Int = System.currentTimeMillis().toInt()
    ) {
        val triggerTimeMillis = System.currentTimeMillis() + delayMillis

        Log.d(TAG, "Scheduling reminder with message: '$message' for $delayMillis minutes from now")
        Log.d(TAG, "Trigger time: $triggerTimeMillis (current: ${System.currentTimeMillis()})")

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_MESSAGE, message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Use setExactAndAllowWhileIdle for precise timing even in Doze mode
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTimeMillis,
            pendingIntent
        )
        Log.d(TAG, "Using setExactAndAllowWhileIdle")

        Log.d(TAG, "Reminder scheduled with request code: $requestCode")
    }

    fun cancelReminder(requestCode: Int) {
        Log.d(TAG, "Cancelling reminder with request code: $requestCode")

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    companion object {
        private const val TAG = "ReminderManager"
    }
}