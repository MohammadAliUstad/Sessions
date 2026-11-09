package com.yugentech.sessions.notification.viewmodel

import com.yugentech.sessions.R
import android.text.format.DateFormat
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.notification.datastore.NotificationDataStore
import com.yugentech.sessions.notification.model.NotificationConfig
import com.yugentech.sessions.notification.reminders.ReminderManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

/** The reminder switches and time on the settings screen, the same in Quill and Sessions. */
class NotificationViewModel(
    private val reminders: ReminderManager,
    private val notificationDataStore: NotificationDataStore
) : ViewModel() {

    val notificationConfig: StateFlow<NotificationConfig> =
        notificationDataStore.notificationConfigFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = NotificationConfig()
            )

    private val _showExactAlarmDialog = MutableStateFlow(value = false)
    val showExactAlarmDialog = _showExactAlarmDialog.asStateFlow()

    // Set when the user is sent to system settings for the exact-alarm permission, so that
    // coming back with it granted can finish what they were doing instead of making them
    // tap the reminder toggle again.
    private var awaitingExactAlarmPermission = false

    fun dismissPermissionDialog() {
        _showExactAlarmDialog.value = false
    }

    // User backed out of the permission dialog without going to settings.
    fun cancelPermissionRequest() {
        awaitingExactAlarmPermission = false
        _showExactAlarmDialog.value = false
    }

    fun canEnableReminders(): Boolean {
        val hasPermission = reminders.canScheduleExactAlarms()
        if (!hasPermission) {
            requestExactAlarmPermission()
            return false
        }
        return true
    }

    // Called when the screen resumes. Returns true if the user just came back from granting
    // the exact-alarm permission while trying to turn the reminder on, meaning the time
    // picker should open to finish that. If the reminder was already on, it's rescheduled
    // here directly instead.
    fun onReturnedFromSettings(): Boolean {
        if (!awaitingExactAlarmPermission || !reminders.canScheduleExactAlarms()) return false
        awaitingExactAlarmPermission = false

        val config = notificationConfig.value
        if (config.notificationsEnabled && config.dailyReminderEnabled) {
            reminders.scheduleDailyReminder(config.reminderHour, config.reminderMinute)
            return false
        }
        return true
    }

    private fun requestExactAlarmPermission() {
        awaitingExactAlarmPermission = true
        _showExactAlarmDialog.value = true
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            notificationDataStore.setNotificationsEnabled(enabled)
            reminders.apply(notificationConfig.value.copy(notificationsEnabled = enabled))
        }
    }

    fun setDailyReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            notificationDataStore.setDailyReminderEnabled(enabled)
            reminders.apply(notificationConfig.value.copy(dailyReminderEnabled = enabled))
        }
    }

    fun setPlayfulRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            notificationDataStore.setPlayfulRemindersEnabled(enabled)
            reminders.apply(notificationConfig.value.copy(playfulRemindersEnabled = enabled))
        }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            notificationDataStore.setReminderTime(hour, minute)
            notificationDataStore.setDailyReminderEnabled(true)
            // Schedule with the time just picked, not notificationConfig.value -- the saved
            // time only reaches that StateFlow after DataStore emits, so reading it back here
            // can still return the previous time.
            reminders.apply(
                notificationConfig.value.copy(
                    dailyReminderEnabled = true,
                    reminderHour = hour,
                    reminderMinute = minute
                )
            )
        }
    }

    /**
     * The daily reminder row's subtitle. The title already says "daily", so this names the part
     * of the day instead: "Every evening at 7:00 PM".
     */
    fun formatReminderTime(context: Context): String {
        val config = notificationConfig.value
        if (!config.dailyReminderEnabled) {
            return context.getString(R.string.reminder_pick_time)
        }

        val hour = config.reminderHour
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, config.reminderMinute)
        }
        // Whole sentences per part of the day, since word order differs between languages
        val sentence = when (hour) {
            in 5..11 -> R.string.reminder_every_morning
            in 12..16 -> R.string.reminder_every_afternoon
            in 17..20 -> R.string.reminder_every_evening
            else -> R.string.reminder_every_night
        }
        val time = DateFormat.getTimeFormat(context).format(calendar.time)
        return context.getString(sentence, time)
    }

    /** Cancels every reminder, for signing out. */
    fun cancelReminders() {
        reminders.cancelAll()
    }
}
