package com.yugentech.sessions.theme.themeRepository

import com.yugentech.sessions.theme.ThemeService
import com.yugentech.sessions.theme.utils.ThemeConfiguration

class ThemeRepositoryImpl(
    private val service: ThemeService
) : ThemeRepository {
    override val themeConfiguration = service.themeConfiguration
    override suspend fun setThemeConfig(config: ThemeConfiguration) =
        service.updateThemeConfig(config)

    override suspend fun resetThemeToDefaults() = service.resetToDefaults()
}