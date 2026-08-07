package com.yugentech.sessions.notification.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.yugentech.sessions.MainActivity
import com.yugentech.sessions.R
import com.yugentech.sessions.notification.active.ActiveForeground
import com.yugentech.sessions.notification.model.Notification
import com.yugentech.sessions.notification.model.NotificationType
import com.yugentech.sessions.timer.repository.TimerRepository
import com.yugentech.sessions.timer.state.TimerMode
import com.yugentech.sessions.timer.state.TimerState
import com.yugentech.sessions.ui.dash.util.SessionDashboardCalculator
import com.yugentech.sessions.utils.AppConstants
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.util.Locale

class NotificationService(
    private val context: Context
) : KoinComponent {

    private val notificationManager = NotificationManagerCompat.from(context)
    private val timerRepository: TimerRepository by inject()

    companion object {
        const val ACTIVE_CHANNEL_ID = "active_session_channel"
        const val REMINDER_CHANNEL_ID = "reminder_channel"
        const val EXTRA_NAVIGATE_TO_HOME = "navigate_to_home"

        const val ACTIVE_NOTIFICATION_ID = 1001
        const val REMINDER_NOTIFICATION_ID = 1002

        private const val RC_OPEN_APP = 0
        private const val RC_PAUSE_RESUME = 1
        private const val RC_SKIP = 2
        private const val RC_FINISH = 3

        private const val PROGRESS_MAX_PERCENT = 100
    }

    // region Channels

    fun createNotificationChannels() {
        Timber.d("Creating notification channels")
        notificationManager.createNotificationChannel(buildActiveChannel())
        notificationManager.createNotificationChannel(buildReminderChannel())
    }

    private fun buildActiveChannel(): NotificationChannel =
        NotificationChannel(
            ACTIVE_CHANNEL_ID,
            "Active Session",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows when a study session is active"
            setSound(null, null)
            enableVibration(false)
        }

    private fun buildReminderChannel(): NotificationChannel =
        NotificationChannel(
            REMINDER_CHANNEL_ID,
            "Reminder",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminds you to start or resume a study session"
        }

    // endregion

    // region Public API

    fun showNotification(notification: Notification) {
        if (!hasNotificationPermission()) {
            Timber.w("Cannot show notification: POST_NOTIFICATIONS permission missing")
            return
        }
        try {
            notificationManager.notify(notification.id, buildNotification(notification))
        } catch (e: SecurityException) {
            Timber.e(e, "SecurityException while showing notification")
        } catch (e: Exception) {
            Timber.e(e, "Failed to show notification")
        }
    }

    fun hideNotification(notificationId: Int) {
        try {
            notificationManager.cancel(notificationId)
        } catch (e: Exception) {
            Timber.e(e, "Failed to cancel notification ID: $notificationId")
        }
    }

    fun buildNotification(notification: Notification): android.app.Notification {
        val builder = baseBuilder(notification)

        when (notification.type) {
            NotificationType.ACTIVE -> applyActiveStyle(builder, notification)
            NotificationType.SCHEDULED -> applyScheduledStyle(builder, notification)
        }

        return builder.build()
    }

    // endregion

    // region Builders

    private fun baseBuilder(notification: Notification): NotificationCompat.Builder {
        val channelId = when (notification.type) {
            NotificationType.SCHEDULED -> REMINDER_CHANNEL_ID
            NotificationType.ACTIVE -> ACTIVE_CHANNEL_ID
        }

        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(notification.title)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(openAppIntent())
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setOngoing(notification.isOngoing)
            .setAutoCancel(!notification.isOngoing)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    setForegroundServiceBehavior(
                        android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE
                    )
                }
            }
    }

    private fun applyActiveStyle(
        builder: NotificationCompat.Builder,
        notification: Notification
    ) {
        builder.setSilent(true)

        val timerState = timerRepository.timerState.value
        val content = activeContent(notification, timerState)

        builder.setContentTitle(content.title)
        builder.setContentText(content.text)

        addSessionActions(builder, timerState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            attachLiveProgress(builder, notification, timerState, content.formattedTime)
        } else {
            attachBasicProgress(builder, notification, timerState)
        }
    }

    private fun applyScheduledStyle(builder: NotificationCompat.Builder, notification: Notification) {
        builder
            .setContentText(notification.message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
    }

    // endregion

    // region Active content

    private data class ActiveContent(
        val title: String,
        val text: String,
        val formattedTime: String?
    )

    private fun activeContent(
        notification: Notification,
        timerState: TimerState
    ): ActiveContent {
        val dashboardState = SessionDashboardCalculator.calculate(timerState)

        val formattedTime = notification.remainingSeconds?.let { seconds ->
            String.format(Locale.US, "%02d:%02d", seconds / 60, seconds % 60)
        }

        val title = buildTitle(notification)
        val text = if (formattedTime != null) {
            "$formattedTime remaining  •  ${dashboardState.subMessage}"
        } else {
            dashboardState.subMessage
        }

        return ActiveContent(title, text, formattedTime)
    }

    private fun buildTitle(notification: Notification): String {
        val modeTitle = when (notification.mode) {
            TimerMode.Focus -> "Focus Time"
            TimerMode.ShortBreak -> "Short Break"
            TimerMode.LongBreak -> "Long Break"
            else -> ""
        }

        val targetSets = notification.totalSets ?: 0
        val completedSets = notification.completedSets ?: 0
        val setsLeft = (targetSets - completedSets).coerceAtLeast(0)

        val setsText = when {
            setsLeft == 1 -> "1 set to go"
            setsLeft > 1 -> "$setsLeft sets to go"
            else -> "Finish line!"
        }

        return if (modeTitle.isNotEmpty()) "$modeTitle – $setsText" else setsText
    }

    // endregion

    // region Actions

    private fun addSessionActions(builder: NotificationCompat.Builder, state: TimerState) {
        builder.addAction(pauseResumeAction(state))
        if (state.timerConfig.targetSets > 1) {
            builder.addAction(
                action(
                    android.R.drawable.ic_media_next,
                    "Skip",
                    AppConstants.ACTION_SKIP_SESSION,
                    RC_SKIP
                )
            )
        }
        builder.addAction(
            action(
                android.R.drawable.ic_menu_save,
                "Finish",
                AppConstants.ACTION_FINISH_SESSION,
                RC_FINISH
            )
        )
    }

    private fun pauseResumeAction(state: TimerState): NotificationCompat.Action =
        if (state.isTimerRunning) {
            action(
                android.R.drawable.ic_media_pause,
                "Pause",
                AppConstants.ACTION_PAUSE_SESSION,
                RC_PAUSE_RESUME
            )
        } else {
            action(
                android.R.drawable.ic_media_play,
                "Resume",
                AppConstants.ACTION_RESUME_SESSION,
                RC_PAUSE_RESUME
            )
        }

    private fun action(
        iconRes: Int,
        title: String,
        intentAction: String,
        requestCode: Int
    ): NotificationCompat.Action =
        NotificationCompat.Action.Builder(
            iconRes,
            title,
            servicePendingIntent(intentAction, requestCode)
        ).build()

    // endregion

    // region Progress (all API levels)

    // Overall session progress as a 0–100 value, spanning all sets.
    // Focus sets each contribute an equal share; breaks sit at the boundary between sets.
    private fun computeOverallProgress(notification: Notification, timerState: TimerState): Int {
        val targetSets = timerState.timerConfig.targetSets.coerceAtLeast(1)
        val completedSets = timerState.completedSets
        val isBreakMode = timerState.currentMode != TimerMode.Focus

        return if (isBreakMode) {
            (completedSets.toFloat() / targetSets * PROGRESS_MAX_PERCENT).toInt()
        } else {
            val totalSeconds = (notification.totalSeconds ?: 1L).coerceAtLeast(1L)
            val remainingSeconds = notification.remainingSeconds ?: 0L
            val elapsedFraction = (totalSeconds - remainingSeconds).toFloat() / totalSeconds
            ((completedSets + elapsedFraction) / targetSets * PROGRESS_MAX_PERCENT).toInt()
        }.coerceIn(0, PROGRESS_MAX_PERCENT)
    }

    // Basic progress bar for pre-Android 16 — overall session progress across all sets.
    private fun attachBasicProgress(
        builder: NotificationCompat.Builder,
        notification: Notification,
        timerState: TimerState
    ) {
        val overall = computeOverallProgress(notification, timerState)
        builder.setProgress(PROGRESS_MAX_PERCENT, overall, false)
    }

    // Android 16+ segmented progress bar. All segments have explicit colours so they always
    // render at full visual thickness. setStyledByProgress(true) lets the system light up the
    // "filled" region and dim the "unfilled" region. We drive a discrete progress value that
    // jumps to the end of whichever block just became active, so each segment flips fully on
    // the moment its phase starts rather than filling second-by-second.
    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    private fun attachLiveProgress(
        builder: NotificationCompat.Builder,
        notification: Notification,
        timerState: TimerState,
        formattedTime: String?
    ) {
        val config = timerState.timerConfig
        val targetSets = config.targetSets.coerceAtLeast(1)
        val completedSets = timerState.completedSets
        val isFocusMode = timerState.currentMode == TimerMode.Focus

        val primaryColor     = context.getColor(android.R.color.system_accent1_200)
        val shortBreakColor  = context.getColor(android.R.color.system_accent3_200)
        val longBreakColor   = Color.argb(255, 255, 180, 171)

        val breakLen = 1
        val totalBreakLen = (targetSets - 1).coerceAtLeast(0) * breakLen
        val totalFocusLen = PROGRESS_MAX_PERCENT - totalBreakLen
        val baseFocusLen = (totalFocusLen / targetSets).coerceAtLeast(1)
        val extraUnits = totalFocusLen - baseFocusLen * targetSets

        var discreteProgress = 0
        var pos = 0
        outer@ for (i in 1..targetSets) {
            pos += baseFocusLen + if (i <= extraUnits) 1 else 0
            if (isFocusMode && i == completedSets + 1) { discreteProgress = pos; break@outer }
            if (i < targetSets) {
                pos += breakLen
                if (!isFocusMode && i == completedSets) { discreteProgress = pos; break@outer }
            }
        }

        val progressStyle = NotificationCompat.ProgressStyle()
            .setProgress(discreteProgress.coerceIn(0, PROGRESS_MAX_PERCENT))
            .setStyledByProgress(true)

        for (i in 1..targetSets) {
            val focusLen = baseFocusLen + if (i <= extraUnits) 1 else 0
            progressStyle.addProgressSegment(
                NotificationCompat.ProgressStyle.Segment(focusLen).setColor(primaryColor)
            )
            if (i < targetSets) {
                val isLongBreak = config.longBreakEnabled && (i % config.setsPerLongBreak == 0)
                val segmentColor = if (isLongBreak) longBreakColor else shortBreakColor
                progressStyle.addProgressSegment(
                    NotificationCompat.ProgressStyle.Segment(breakLen).setColor(segmentColor)
                )
            }
        }

        builder
            .setStyle(progressStyle)
            .setProgress(PROGRESS_MAX_PERCENT, discreteProgress, false)
            .setRequestPromotedOngoing(true)

        formattedTime?.let { builder.setShortCriticalText(it) }
    }

    // endregion

    // region Intents

    private fun openAppIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            // SINGLE_TOP: if MainActivity is already at the top of its task (always true for a
            // single-Activity app), onNewIntent is called instead of recreating the whole app.
            // NEW_TASK: required when starting an Activity from a non-Activity context (Service).
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_NAVIGATE_TO_HOME, true)
        }
        return PendingIntent.getActivity(
            context,
            RC_OPEN_APP,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }


    private fun servicePendingIntent(action: String, requestCode: Int): PendingIntent {
        val intent = Intent(context, ActiveForeground::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    // endregion

    // region Permissions

    private fun hasNotificationPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            notificationManager.areNotificationsEnabled()
        }

    // endregion
}
