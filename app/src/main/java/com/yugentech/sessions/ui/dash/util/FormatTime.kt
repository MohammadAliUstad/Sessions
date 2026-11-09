package com.yugentech.sessions.ui.dash.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yugentech.sessions.R

/** A total time as hours and minutes ("2 hr 15 min"), in the app's language; "0" when there's none. */
@Composable
fun formatTime(seconds: Long): String {
    val hours = (seconds / 3600).toInt()
    val minutes = ((seconds % 3600) / 60).toInt()
    return when {
        hours > 0 && minutes > 0 -> stringResource(R.string.duration_hr_min, hours, minutes)
        hours > 0 -> stringResource(R.string.duration_hr, hours)
        minutes > 0 -> stringResource(R.string.duration_minutes, minutes)
        else -> "0"
    }
}

/** What a session saved without a task name is called; it's stored in English, so shown translated. */
const val DEFAULT_TASK_NAME = "Focus Session"

/** A session's task as shown: the user's own words, or "Focus Session" in the app's language. */
@Composable
fun displayTaskName(task: String): String =
    if (task.isBlank() || task == DEFAULT_TASK_NAME) stringResource(R.string.default_task_name) else task
