package com.yugentech.sessions.ui.dash.util

import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import com.yugentech.sessions.R
import java.util.Calendar
import java.util.Date
import java.util.Locale

/*
 * The history's group headers, written the way the app's language writes dates: the patterns are
 * the locale's best fit for a set of fields (day name, month, day...), so the order, words and
 * punctuation follow the language rather than English.
 */

private fun locale(context: Context): Locale = context.resources.configuration.locales[0]

private fun format(context: Context, skeleton: String, timestamp: Long): String {
    val locale = locale(context)
    val pattern = DateFormat.getBestDateTimePattern(locale, skeleton)
    return java.text.SimpleDateFormat(pattern, locale).format(Date(timestamp))
}

/** "Today", "Yesterday", or the weekday and date. */
fun dateHeader(context: Context, timestamp: Long): String {
    val sessionCalendar = Calendar.getInstance().apply { timeInMillis = timestamp }
    val todayCalendar = Calendar.getInstance()
    val yesterdayCalendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    return when {
        isSameDay(sessionCalendar, todayCalendar) -> context.getString(R.string.date_today)
        isSameDay(sessionCalendar, yesterdayCalendar) -> context.getString(R.string.date_yesterday)
        else -> format(context, "EEEEMMMdd", timestamp)
    }
}

/** The week (Monday to Sunday) the session falls in, as a date range. */
fun weeklyHeader(context: Context, timestamp: Long): String {
    val cal = Calendar.getInstance().apply {
        timeInMillis = timestamp
        firstDayOfWeek = Calendar.MONDAY
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    }
    val start = cal.timeInMillis
    cal.add(Calendar.DAY_OF_WEEK, 6)
    val end = cal.timeInMillis
    return DateUtils.formatDateRange(
        context,
        start,
        end,
        DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_ABBREV_MONTH
    )
}

/** The month and year. */
fun monthlyHeader(context: Context, timestamp: Long): String = format(context, "MMMMyyyy", timestamp)

fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
