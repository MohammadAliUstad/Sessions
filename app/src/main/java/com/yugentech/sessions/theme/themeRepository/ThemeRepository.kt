package com.yugentech.sessions.theme.themeRepository

import com.yugentech.sessions.ui.theme.ColorTheme
import com.yugentech.sessions.ui.theme.ThemeConfig
import com.yugentech.sessions.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

abstract class ThemeRepository {
    abstract val themeConfig: Flow<ThemeConfig>
    abstract suspend fun setThemeMode(themeMode: ThemeMode)
    abstract suspend fun setColorTheme(colorTheme: ColorTheme)
    abstract suspend fun setUseDynamicColors(useDynamicColors: Boolean)
    abstract suspend fun setThemeConfig(themeConfig: ThemeConfig)
    abstract suspend fun resetThemeToDefaults()
}