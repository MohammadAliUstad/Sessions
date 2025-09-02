package com.yugentech.sessions.theme

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yugentech.sessions.theme.utils.ColorTheme
import com.yugentech.sessions.theme.utils.ThemeConfiguration
import com.yugentech.sessions.theme.utils.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class ThemeService(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val COLOR_THEME_KEY = stringPreferencesKey("color_theme")
        private val USE_DYNAMIC_COLORS_KEY = booleanPreferencesKey("use_dynamic_colors")
    }

    val themeConfiguration: Flow<ThemeConfiguration> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            ThemeConfiguration(
                themeMode = ThemeMode.valueOf(prefs[THEME_MODE_KEY] ?: ThemeMode.LIGHT.name),
                colorTheme = ColorTheme.valueOf(prefs[COLOR_THEME_KEY] ?: ColorTheme.DYNAMIC.name),
                useDynamicColors = prefs[USE_DYNAMIC_COLORS_KEY] ?: true
            )
        }

    suspend fun updateThemeConfig(config: ThemeConfiguration) {
        dataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = config.themeMode.name
            prefs[COLOR_THEME_KEY] = config.colorTheme.name
            prefs[USE_DYNAMIC_COLORS_KEY] = config.useDynamicColors
        }
    }

    suspend fun resetToDefaults() {
        dataStore.edit { it.clear() }
    }
}