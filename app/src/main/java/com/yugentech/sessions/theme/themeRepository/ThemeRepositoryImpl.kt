package com.yugentech.sessions.theme.themeRepository

import com.yugentech.sessions.theme.ThemeService
import com.yugentech.sessions.theme.models.ThemeConfiguration
import timber.log.Timber

class ThemeRepositoryImpl(
    private val service: ThemeService
) : ThemeRepository {

    override val themeConfiguration = service.themeConfiguration

    override suspend fun setThemeConfig(config: ThemeConfiguration) {
        Timber.i("Updating theme configuration: Mode=${config.themeMode}, Color=${config.colorTheme}")
        service.updateThemeConfig(config)
    }

    override suspend fun resetThemeToDefaults() {
        Timber.d("Resetting theme to defaults")
        service.resetToDefaults()
    }
}