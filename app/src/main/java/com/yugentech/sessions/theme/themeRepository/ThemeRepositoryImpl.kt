package com.yugentech.sessions.theme.themeRepository

import com.yugentech.sessions.theme.ThemeService
import com.yugentech.sessions.theme.utils.ColorTheme
import com.yugentech.sessions.theme.utils.ThemeConfiguration
import com.yugentech.sessions.theme.utils.ThemeMode
import kotlinx.coroutines.flow.Flow

class ThemeRepositoryImpl(
    private val themeService: ThemeService
) : ThemeRepository() {

    override val themeConfiguration: Flow<ThemeConfiguration> = themeService.themeConfiguration

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        themeService.updateThemeMode(themeMode)
    }

    override suspend fun setColorTheme(colorTheme: ColorTheme) {
        themeService.updateColorTheme(colorTheme)
    }

    override suspend fun setUseDynamicColors(useDynamicColors: Boolean) {
        themeService.updateUseDynamicColors(useDynamicColors)
    }

    override suspend fun setThemeConfig(themeConfiguration: ThemeConfiguration) {
        themeService.updateThemeConfig(themeConfiguration)
    }

    override suspend fun resetThemeToDefaults() {
        themeService.resetToDefaults()
    }
}