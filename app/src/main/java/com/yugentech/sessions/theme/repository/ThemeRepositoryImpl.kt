package com.yugentech.sessions.theme.repository

import com.yugentech.sessions.theme.datastore.ThemeDataStore
import com.yugentech.sessions.theme.config.ThemeConfiguration
import timber.log.Timber

class ThemeRepositoryImpl(
    private val dataStore: ThemeDataStore
) : ThemeRepository {

    // Delegate the flow directly to the service layer
    override val themeConfiguration = dataStore.themeConfiguration

    override suspend fun setThemeConfig(config: ThemeConfiguration) {
        Timber.i("Updating theme configuration: Mode=${config.themeMode}, Color=${config.colorTheme}")
        // Pass the new configuration to the service for persistence
        dataStore.updateThemeConfig(config)
    }

    override suspend fun resetThemeToDefaults() {
        Timber.d("Resetting theme to defaults")
        // Trigger the service to clear custom settings
        dataStore.resetToDefaults()
    }
}