package com.yugentech.sessions.theme

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yugentech.sessions.theme.models.ColorTheme
import com.yugentech.sessions.theme.models.ThemeConfiguration
import com.yugentech.sessions.theme.models.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

// Service managing persistence of theme preferences via DataStore
class ThemeService(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val COLOR_THEME_KEY = stringPreferencesKey("color_theme")
        private val USE_DYNAMIC_COLORS_KEY = booleanPreferencesKey("use_dynamic_colors")

        // New key for persisting the AMOLED mode toggle
        private val IS_AMOLED_MODE_KEY = booleanPreferencesKey("is_amoled_mode")
    }

    // Exposes current theme config as a flow, defaulting to standard values on error
    val themeConfiguration: Flow<ThemeConfiguration> = dataStore.data
        .catch {
            Timber.e(it, "Error reading theme preferences")
            emit(emptyPreferences())
        }
        .map { prefs ->
            ThemeConfiguration(
                themeMode = ThemeMode.valueOf(prefs[THEME_MODE_KEY] ?: ThemeMode.LIGHT.name),
                colorTheme = ColorTheme.valueOf(prefs[COLOR_THEME_KEY] ?: ColorTheme.DYNAMIC.name),
                useDynamicColors = prefs[USE_DYNAMIC_COLORS_KEY] ?: true,
                isAmoledMode = prefs[IS_AMOLED_MODE_KEY] ?: false
            )
        }

    // Persists the new theme configuration
    suspend fun updateThemeConfig(config: ThemeConfiguration) {
        Timber.d("Saving theme config: Mode=${config.themeMode}, Color=${config.colorTheme}, Amoled=${config.isAmoledMode}")
        dataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = config.themeMode.name
            prefs[COLOR_THEME_KEY] = config.colorTheme.name
            prefs[USE_DYNAMIC_COLORS_KEY] = config.useDynamicColors

            // Save AMOLED preference
            prefs[IS_AMOLED_MODE_KEY] = config.isAmoledMode
        }
    }

    // Clears all theme settings, reverting to app defaults
    suspend fun resetToDefaults() {
        Timber.i("Resetting theme preferences to default")
        dataStore.edit { it.clear() }
    }
}