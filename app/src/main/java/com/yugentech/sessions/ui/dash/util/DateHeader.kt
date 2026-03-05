package com.yugentech.sessions.ui.dash.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun dateHeader(timestamp: Long): String {
    val sessionCalendar = Calendar.getInstance().apply { timeInMillis = timestamp }
    val todayCalendar = Calendar.getInstance()
    val yesterdayCalendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    return when {
        isSameDay(sessionCalendar, todayCalendar) -> "Today"
        isSameDay(sessionCalendar, yesterdayCalendar) -> "Yesterday"
        else -> {
            val formatter = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
            formatter.format(Date(timestamp))
        }
    }
}

fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}