package com.yugentech.sessions.theme.themeRepository

import com.yugentech.sessions.theme.ThemeService
import com.yugentech.sessions.ui.theme.ColorTheme
import com.yugentech.sessions.ui.theme.ThemeConfig
import com.yugentech.sessions.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

class ThemeRepositoryImpl(
    private val themeService: ThemeService
) : ThemeRepository() {

    override val themeConfig: Flow<ThemeConfig> = themeService.themeConfig

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        themeService.updateThemeMode(themeMode)
    }

    override suspend fun setColorTheme(colorTheme: ColorTheme) {
        themeService.updateColorTheme(colorTheme)
    }

    override suspend fun setUseDynamicColors(useDynamicColors: Boolean) {
        themeService.updateUseDynamicColors(useDynamicColors)
    }

    override suspend fun setThemeConfig(themeConfig: ThemeConfig) {
        themeService.updateThemeConfig(themeConfig)
    }

    override suspend fun resetThemeToDefaults() {
        themeService.resetToDefaults()
    }
}