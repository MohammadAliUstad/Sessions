package com.yugentech.sessions.theme.themeRepository

import com.yugentech.sessions.theme.models.ThemeConfiguration
import kotlinx.coroutines.flow.Flow

// Interface exposing theme preference management
interface ThemeRepository {
    // Observable flow of current theme settings
    val themeConfiguration: Flow<ThemeConfiguration>

    // Updates user theme preferences
    suspend fun setThemeConfig(config: ThemeConfiguration)

    // Resets theme to default values
    suspend fun resetThemeToDefaults()
}