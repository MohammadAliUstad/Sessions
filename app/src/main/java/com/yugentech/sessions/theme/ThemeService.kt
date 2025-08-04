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
        .catch { exception ->
            exception.printStackTrace()
            emit(emptyPreferences())
        }
        .map { preferences ->
            ThemeConfiguration(
                themeMode = try {
                    ThemeMode.valueOf(
                        preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
                    )
                } catch (_: IllegalArgumentException) {
                    ThemeMode.SYSTEM
                },
                colorTheme = try {
                    ColorTheme.valueOf(
                        preferences[COLOR_THEME_KEY] ?: ColorTheme.DYNAMIC.name
                    )
                } catch (_: IllegalArgumentException) {
                    ColorTheme.DYNAMIC
                },
                useDynamicColors = preferences[USE_DYNAMIC_COLORS_KEY] ?: true
            )
        }

    suspend fun updateThemeMode(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = themeMode.name
        }
    }

    suspend fun updateColorTheme(colorTheme: ColorTheme) {
        dataStore.edit { preferences ->
            preferences[COLOR_THEME_KEY] = colorTheme.name
        }
    }

    suspend fun updateUseDynamicColors(useDynamicColors: Boolean) {
        dataStore.edit { preferences ->
            preferences[USE_DYNAMIC_COLORS_KEY] = useDynamicColors
        }
    }

    suspend fun updateThemeConfig(themeConfiguration: ThemeConfiguration) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = themeConfiguration.themeMode.name
            preferences[COLOR_THEME_KEY] = themeConfiguration.colorTheme.name
            preferences[USE_DYNAMIC_COLORS_KEY] = themeConfiguration.useDynamicColors
        }
    }

    suspend fun resetToDefaults() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}