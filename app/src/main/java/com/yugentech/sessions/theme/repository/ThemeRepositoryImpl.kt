package com.yugentech.sessions.theme.repository

import com.yugentech.sessions.theme.datastore.ThemeDataStore
import com.yugentech.sessions.theme.config.ThemeConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ThemeRepositoryImpl(
    private val dataStore: ThemeDataStore,
    externalScope: CoroutineScope
) : ThemeRepository {

    private val _themeConfiguration = MutableStateFlow(ThemeConfiguration())

    // Hot flow keeping the latest theme configuration immediately available
    override val themeConfiguration: StateFlow<ThemeConfiguration> =
        _themeConfiguration.asStateFlow()

    init {
        externalScope.launch {
            dataStore.themeConfiguration.collect { config ->
                _themeConfiguration.value = config
            }
        }
    }

    override suspend fun setThemeConfig(config: ThemeConfiguration) {
        Timber.i("Updating theme configuration: Mode=${config.themeMode}, Color=${config.colorTheme}")
        _themeConfiguration.value = config
        // Pass the new configuration to the service for persistence
        dataStore.updateThemeConfig(config)
    }

    override suspend fun resetThemeToDefaults() {
        Timber.d("Resetting theme to defaults")
        // Trigger the service to clear custom settings
        dataStore.resetToDefaults()
    }
}