package com.yugentech.sessions.theme.themeRepository

import com.yugentech.sessions.theme.utils.ColorTheme
import com.yugentech.sessions.theme.utils.ThemeConfiguration
import com.yugentech.sessions.theme.utils.ThemeMode
import kotlinx.coroutines.flow.Flow

abstract class ThemeRepository {
    abstract val themeConfiguration: Flow<ThemeConfiguration>
    abstract suspend fun setThemeMode(themeMode: ThemeMode)
    abstract suspend fun setColorTheme(colorTheme: ColorTheme)
    abstract suspend fun setUseDynamicColors(useDynamicColors: Boolean)
    abstract suspend fun setThemeConfig(themeConfiguration: ThemeConfiguration)
    abstract suspend fun resetThemeToDefaults()
}