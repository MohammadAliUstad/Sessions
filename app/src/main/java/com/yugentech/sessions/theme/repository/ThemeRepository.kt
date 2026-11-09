package com.yugentech.sessions.theme.repository

import com.yugentech.sessions.theme.config.ThemeConfiguration
import kotlinx.coroutines.flow.StateFlow

interface ThemeRepository {
    // Provides a real-time stream of the current theme settings
    val themeConfiguration: StateFlow<ThemeConfiguration>

    // Updates the current theme configuration with new values
    suspend fun setThemeConfig(config: ThemeConfiguration)

    // Reverts all theme settings back to the factory defaults
    suspend fun resetThemeToDefaults()
}