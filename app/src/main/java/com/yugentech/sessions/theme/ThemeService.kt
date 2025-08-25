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
import android.util.Log

class ThemeService(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val COLOR_THEME_KEY = stringPreferencesKey("color_theme")
        private val USE_DYNAMIC_COLORS_KEY = booleanPreferencesKey("use_dynamic_colors")
        private const val TAG = "ThemeService"
    }

    val themeConfiguration: Flow<ThemeConfiguration> = dataStore.data
        .catch { exception ->
            emit(emptyPreferences())
        }
        .map { preferences ->
            val config = ThemeConfiguration(
                themeMode = try {
                    ThemeMode.valueOf(
                        preferences[THEME_MODE_KEY] ?: ThemeMode.LIGHT.name
                    )
                } catch (_: IllegalArgumentException) {
                    ThemeMode.LIGHT
                },
                colorTheme = try {
                    ColorTheme.valueOf(
                        preferences[COLOR_THEME_KEY] ?: ColorTheme.DYNAMIC.name
                    )
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color theme, using DYNAMIC", e)
                    ColorTheme.DYNAMIC
                },
                useDynamicColors = preferences[USE_DYNAMIC_COLORS_KEY] ?: true
            )
            config
        }

    suspend fun updateThemeMode(themeMode: ThemeMode) {
        try {
            dataStore.edit { preferences ->
                preferences[THEME_MODE_KEY] = themeMode.name
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateColorTheme(colorTheme: ColorTheme) {
        try {
            dataStore.edit { preferences ->
                preferences[COLOR_THEME_KEY] = colorTheme.name
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateUseDynamicColors(useDynamicColors: Boolean) {
        try {
            dataStore.edit { preferences ->
                preferences[USE_DYNAMIC_COLORS_KEY] = useDynamicColors
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateThemeConfig(themeConfiguration: ThemeConfiguration) {
        try {
            dataStore.edit { preferences ->
                preferences[THEME_MODE_KEY] = themeConfiguration.themeMode.name
                preferences[COLOR_THEME_KEY] = themeConfiguration.colorTheme.name
                preferences[USE_DYNAMIC_COLORS_KEY] = themeConfiguration.useDynamicColors
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun resetToDefaults() {
        try {
            dataStore.edit { preferences ->
                preferences.clear()
            }
        } catch (e: Exception) {
            throw e
        }
    }
}