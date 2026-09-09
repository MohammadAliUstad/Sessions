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

fun weeklyHeader(timestamp: Long): String {
    val cal = Calendar.getInstance().apply {
        timeInMillis = timestamp
        firstDayOfWeek = Calendar.MONDAY
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    }
    val startCal = cal.clone() as Calendar
    val startOfWeek = cal.time

    cal.add(Calendar.DAY_OF_WEEK, 6)
    val endCal = cal.clone() as Calendar
    val endOfWeek = cal.time

    val startMonthFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    val endMonthFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    val dayOnlyFormat = SimpleDateFormat("d", Locale.getDefault())

    val startYear = startCal.get(Calendar.YEAR)
    val endYear = endCal.get(Calendar.YEAR)
    val startMonth = startCal.get(Calendar.MONTH)
    val endMonth = endCal.get(Calendar.MONTH)

    return when {
        startYear != endYear -> {
            "${startMonthFormat.format(startOfWeek)}, $startYear – ${endMonthFormat.format(endOfWeek)}, $endYear"
        }
        startMonth != endMonth -> {
            "${startMonthFormat.format(startOfWeek)} – ${endMonthFormat.format(endOfWeek)}, $startYear"
        }
        else -> {
            "${startMonthFormat.format(startOfWeek)} – ${dayOnlyFormat.format(endOfWeek)}, $startYear"
        }
    }
}

fun monthlyHeader(timestamp: Long): String {
    val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
