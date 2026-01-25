package com.yugentech.sessions.theme

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yugentech.sessions.theme.getters.AppFont
import com.yugentech.sessions.theme.models.ColorTheme
import com.yugentech.sessions.theme.models.ThemeConfiguration
import com.yugentech.sessions.theme.models.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

class ThemeService(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        // Define persistent keys for storing theme settings
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val COLOR_THEME_KEY = stringPreferencesKey("color_theme")
        private val USE_DYNAMIC_COLORS_KEY = booleanPreferencesKey("use_dynamic_colors")
        private val IS_AMOLED_MODE_KEY = booleanPreferencesKey("is_amoled_mode")
        private val APP_FONT_KEY = stringPreferencesKey("app_font")
    }

    // Stream that converts raw DataStore preferences into a ThemeConfiguration object
    val themeConfiguration: Flow<ThemeConfiguration> = dataStore.data
        .catch {
            Timber.e(it, "Error reading theme preferences")
            emit(emptyPreferences())
        }
        .map { prefs ->
            // Safely convert stored strings back to Enums, defaulting if invalid
            val savedFontName = prefs[APP_FONT_KEY] ?: AppFont.Google.name
            val appFont = try {
                AppFont.valueOf(savedFontName)
            } catch (e: IllegalArgumentException) {
                AppFont.Google
            }

            ThemeConfiguration(
                themeMode = ThemeMode.valueOf(prefs[THEME_MODE_KEY] ?: ThemeMode.LIGHT.name),
                colorTheme = ColorTheme.valueOf(prefs[COLOR_THEME_KEY] ?: ColorTheme.DYNAMIC.name),
                useDynamicColors = prefs[USE_DYNAMIC_COLORS_KEY] ?: true,
                isAmoledMode = prefs[IS_AMOLED_MODE_KEY] ?: false,
                appFont = appFont
            )
        }

    // Saves the current theme configuration to disk
    suspend fun updateThemeConfig(config: ThemeConfiguration) {
        Timber.d("Saving theme config: Font=${config.appFont}, Mode=${config.themeMode}...")
        dataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = config.themeMode.name
            prefs[COLOR_THEME_KEY] = config.colorTheme.name
            prefs[USE_DYNAMIC_COLORS_KEY] = config.useDynamicColors
            prefs[IS_AMOLED_MODE_KEY] = config.isAmoledMode
            prefs[APP_FONT_KEY] = config.appFont.name
        }
    }

    // Clears all stored theme preferences
    suspend fun resetToDefaults() {
        Timber.i("Resetting theme preferences to default")
        dataStore.edit { it.clear() }
    }
}