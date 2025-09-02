package com.yugentech.sessions.theme.themeRepository

import com.yugentech.sessions.theme.utils.ThemeConfiguration
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    val themeConfiguration: Flow<ThemeConfiguration>
    suspend fun setThemeConfig(config: ThemeConfiguration)
    suspend fun resetThemeToDefaults()
}