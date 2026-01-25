package com.yugentech.sessions.theme.themeRepository

import com.yugentech.sessions.theme.models.ThemeConfiguration
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    // Provides a real-time stream of the current theme settings
    val themeConfiguration: Flow<ThemeConfiguration>

    // Updates the current theme configuration with new values
    suspend fun setThemeConfig(config: ThemeConfiguration)

    // Reverts all theme settings back to the factory defaults
    suspend fun resetThemeToDefaults()
}