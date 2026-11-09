package com.yugentech.sessions.theme.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yugentech.sessions.theme.AppFont
import com.yugentech.sessions.theme.config.CardAnimationStyle
import com.yugentech.sessions.theme.config.CardStyle
import com.yugentech.sessions.theme.config.ColorTheme
import com.yugentech.sessions.theme.config.HomeLayout
import com.yugentech.sessions.theme.config.ThemeConfiguration
import com.yugentech.sessions.theme.config.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

class ThemeDataStore(
    private val dataStore: DataStore<Preferences>
) {
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

            val themeMode = try {
                ThemeMode.valueOf(prefs[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name)
            } catch (e: IllegalArgumentException) {
                ThemeMode.SYSTEM
            }
            val colorTheme = try {
                ColorTheme.valueOf(prefs[COLOR_THEME_KEY] ?: ColorTheme.SESSIONS.name)
            } catch (e: IllegalArgumentException) {
                ColorTheme.SESSIONS
            }
            val cardStyle = try {
                CardStyle.valueOf(prefs[CARD_STYLE_KEY] ?: CardStyle.FILLED.name)
            } catch (e: IllegalArgumentException) {
                CardStyle.FILLED
            }
            val cardAnimationStyle = try {
                CardAnimationStyle.valueOf(prefs[CARD_ANIMATION_STYLE_KEY] ?: CardAnimationStyle.NONE.name)
            } catch (e: IllegalArgumentException) {
                CardAnimationStyle.NONE
            }
            val homeLayout = try {
                HomeLayout.valueOf(prefs[HOME_LAYOUT_KEY] ?: HomeLayout.CLASSIC.name)
            } catch (e: IllegalArgumentException) {
                HomeLayout.CLASSIC
            }

            ThemeConfiguration(
                themeMode = themeMode,
                colorTheme = colorTheme,
                useDynamicColors = prefs[USE_DYNAMIC_COLORS_KEY] ?: false,
                isAmoledMode = prefs[IS_AMOLED_MODE_KEY] ?: false,
                appFont = appFont,
                cardStyle = cardStyle,
                cardAnimationStyle = cardAnimationStyle,
                homeLayout = homeLayout,
                showHomeGradient = prefs[SHOW_HOME_GRADIENT_KEY] ?: true
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
            prefs[CARD_STYLE_KEY] = config.cardStyle.name
            prefs[CARD_ANIMATION_STYLE_KEY] = config.cardAnimationStyle.name
            prefs[HOME_LAYOUT_KEY] = config.homeLayout.name
            prefs[SHOW_HOME_GRADIENT_KEY] = config.showHomeGradient
        }
    }

    // Clears all stored theme preferences
    suspend fun resetToDefaults() {
        Timber.i("Resetting theme preferences to default")
        dataStore.edit { it.clear() }
    }

    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val COLOR_THEME_KEY = stringPreferencesKey("color_theme")
        private val USE_DYNAMIC_COLORS_KEY = booleanPreferencesKey("use_dynamic_colors")
        private val IS_AMOLED_MODE_KEY = booleanPreferencesKey("is_amoled_mode")
        private val APP_FONT_KEY = stringPreferencesKey("app_font")
        private val CARD_STYLE_KEY = stringPreferencesKey("card_style")
        private val CARD_ANIMATION_STYLE_KEY = stringPreferencesKey("card_animation_style")
        private val HOME_LAYOUT_KEY = stringPreferencesKey("home_layout")
        private val SHOW_HOME_GRADIENT_KEY = booleanPreferencesKey("show_home_gradient")
    }
}