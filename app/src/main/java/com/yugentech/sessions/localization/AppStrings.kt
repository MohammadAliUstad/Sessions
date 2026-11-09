package com.yugentech.sessions.localization

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

/**
 * Strings for code that has no UI context of its own: services, mappers and view models that
 * hand a finished message to the screen (an auth error, "Session too short").
 *
 * Every lookup goes through [AppLocale.wrap], so it is in the language picked in the app even
 * after a switch below Android 13, where the application context keeps the language it started
 * with. Prefer `stringResource` or [UiText] wherever the text is shown from Compose.
 */
object AppStrings {

    private lateinit var app: Context

    /** Called once from the Application's onCreate. */
    fun init(context: Context) {
        app = context.applicationContext
    }

    private val resources get() = AppLocale.wrap(app).resources

    fun get(@StringRes id: Int, vararg args: Any): String = resources.getString(id, *args)

    fun plural(@PluralsRes id: Int, count: Int, vararg args: Any): String =
        resources.getQuantityString(id, count, *args)
}
