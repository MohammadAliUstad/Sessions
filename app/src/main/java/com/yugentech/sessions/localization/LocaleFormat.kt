package com.yugentech.sessions.localization

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** The app's current locale, read so that a language switch recomposes whatever uses it. */
@Composable
fun currentLocale(): Locale = LocalConfiguration.current.locales[0] ?: Locale.getDefault()

/**
 * Today as weekday, day and month ("MONDAY, 9 NOVEMBER", "9 NOVEMBRE LUNDI"…) in the
 * language's own order. Recomputed only when the language changes.
 */
@Composable
fun rememberTodayLabel(): String {
    val locale = currentLocale()
    return remember(locale) {
        val pattern = DateFormat.getBestDateTimePattern(locale, "EEEEdMMMM")
        SimpleDateFormat(pattern, locale).format(Date()).uppercase(locale)
    }
}
