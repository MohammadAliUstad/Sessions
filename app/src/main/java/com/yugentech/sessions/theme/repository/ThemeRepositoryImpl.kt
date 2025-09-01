package com.yugentech.sessions.theme.themeRepository

import com.yugentech.sessions.theme.ThemeService
import com.yugentech.sessions.theme.models.ThemeConfiguration
import timber.log.Timber

class ThemeRepositoryImpl(
    private val service: ThemeService
) : ThemeRepository {

    // Delegate the flow directly to the service layer
    override val themeConfiguration = service.themeConfiguration

    override suspend fun setThemeConfig(config: ThemeConfiguration) {
        Timber.i("Updating theme configuration: Mode=${config.themeMode}, Color=${config.colorTheme}")
        // Pass the new configuration to the service for persistence
        service.updateThemeConfig(config)
    }

    override suspend fun resetThemeToDefaults() {
        Timber.d("Resetting theme to defaults")
        // Trigger the service to clear custom settings
        service.resetToDefaults()
    }
}