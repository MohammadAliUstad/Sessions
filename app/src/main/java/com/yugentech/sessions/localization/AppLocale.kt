package com.yugentech.sessions.localization

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import androidx.core.content.edit
import java.util.Locale

/**
 * The app's own language, apart from the phone's.
 *
 * Switching never restarts the app or recreates the screen. MainActivity declares
 * `configChanges="locale|layoutDirection|screenLayout"` (a language switch can also flip screenLayout
 * bits; without it the system rebuilds the screen), so a language change reaches the running screen as a
 * configuration change and Compose redraws in place: same back stack, same state, no blink.
 *
 * On Android 13 and up this is the system's per-app language: the choice is stored by the system,
 * shows (and can be changed) under the phone's Settings > Apps > Sessions > Language, and the
 * system delivers the configuration change itself.
 *
 * Below Android 13 there is no such setting, so the choice is kept in the app's own preferences.
 * At startup it is applied by wrapping the app's and the activity's context ([wrap]); a switch
 * while running updates their resources in place and hands the new configuration to the views.
 *
 * `null` throughout means "follow the phone's language".
 */
object AppLocale {

    private const val PREFS = "app_locale"
    private const val KEY_TAG = "language_tag"

    /** The language picked in the app, or null when it follows the phone. */
    fun current(context: Context): AppLanguage? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val locales = context.getSystemService(LocaleManager::class.java).applicationLocales
            if (locales.isEmpty) null else AppLanguage.fromTag(locales[0].toLanguageTag())
        } else {
            AppLanguage.fromTag(storedTag(context))
        }

    /** Switches the app to [language] (null: the phone's language), in place. */
    fun set(activity: Activity, language: AppLanguage?) {
        if (current(activity) == language) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // The system stores it and sends onConfigurationChanged; Compose picks it up from there
            activity.getSystemService(LocaleManager::class.java).applicationLocales =
                if (language == null) LocaleList.getEmptyLocaleList() else LocaleList.forLanguageTags(language.tag)
        } else {
            prefs(activity).edit {
                if (language == null) remove(KEY_TAG) else putString(KEY_TAG, language.tag)
            }
            applyInPlace(activity, language?.let { Locale.forLanguageTag(it.tag) } ?: systemLocale(activity))
        }
    }

    /**
     * [base] in the picked language, for `attachBaseContext` of the app, the activity and the
     * timer service. Below Android 13 only; from 13 the system applies the per-app language itself.
     */
    fun wrap(base: Context): Context {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return base
        val tag = storedTag(base)
        if (tag == null) {
            // Following the phone: undo a default left behind by an earlier pick
            Locale.setDefault(systemLocale(base))
            return base
        }
        val locale = Locale.forLanguageTag(tag)
        // Dates and numbers formatted with the default locale follow along too
        Locale.setDefault(locale)
        val configuration = Configuration(base.resources.configuration).apply {
            setLocales(LocaleList(locale))
            setLayoutDirection(locale)
        }
        return base.createConfigurationContext(configuration)
    }

    /**
     * The phone's own language, for the "System default" row.
     *
     * From Android 13 this asks [LocaleManager.getSystemLocales]: once a per-app language is set,
     * the system applies it to this process's copy of the system resources too, so
     * [Resources.getSystem] would report the picked language instead of the phone's.
     * Below 13 nothing touches the system resources (a pick only updates the app's and the
     * activity's), so they still hold the phone's language. Never [Locale.getDefault], which
     * follows the pick.
     */
    fun systemLocale(context: Context): Locale {
        val locales = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).systemLocales
        } else {
            Resources.getSystem().configuration.locales
        }
        return if (locales.isEmpty) Locale.getDefault() else locales[0]
    }

    /**
     * Below Android 13: points the activity's and the app's resources at [locale] and tells the
     * views, the same way the system does for a handled configuration change. Compose sees a new
     * LocalConfiguration and recomposes every string; a switch to or from Arabic flips the layout.
     */
    @Suppress("DEPRECATION")
    private fun applyInPlace(activity: Activity, locale: Locale) {
        Locale.setDefault(locale)
        // The app's resources serve view models, AppStrings and the timer notification
        listOf(activity.resources, activity.applicationContext.resources).distinct().forEach { res ->
            val updated = Configuration(res.configuration).apply {
                setLocales(LocaleList(locale))
                setLayoutDirection(locale)
            }
            res.updateConfiguration(updated, res.displayMetrics)
        }
        val configuration = activity.resources.configuration
        activity.window.decorView.apply {
            layoutDirection = configuration.layoutDirection
            dispatchConfigurationChanged(Configuration(configuration))
        }
    }

    private fun storedTag(context: Context): String? = prefs(context).getString(KEY_TAG, null)

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
