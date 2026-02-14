package com.yugentech.sessions.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.theme.AppFont
import com.yugentech.sessions.theme.config.ColorTheme
import com.yugentech.sessions.theme.config.ThemeConfiguration
import com.yugentech.sessions.theme.config.ThemeMode
import com.yugentech.sessions.theme.repository.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val repository: ThemeRepository
) : ViewModel() {

    // Internal mutable state for the current theme configuration
    private val _themeConfiguration = MutableStateFlow(
        ThemeConfiguration(
            ThemeMode.LIGHT, ColorTheme.DYNAMIC,
            useDynamicColors = true,
            isAmoledMode = false,
            appFont = AppFont.Google
        )
    )

    // Public read-only stream of theme configuration
    val themeConfiguration: StateFlow<ThemeConfiguration> = _themeConfiguration.asStateFlow()

    // Derived stream specifically for the current font, helpful for UI logic
    val currentFont: StateFlow<AppFont> = _themeConfiguration
        .map { it.appFont }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = AppFont.Google
        )

    init {
        // Automatically update local state whenever the repository data changes
        viewModelScope.launch {
            repository.themeConfiguration.collect { config ->
                _themeConfiguration.value = config
            }
        }
    }

    // Updates the full theme configuration and persists it
    fun updateTheme(config: ThemeConfiguration) {
        _themeConfiguration.value = config

        viewModelScope.launch {
            repository.setThemeConfig(config)
        }
    }

    // Helper method to update just the font while keeping other settings
    fun setFont(font: AppFont) {
        val current = _themeConfiguration.value
        val newConfig = current.copy(appFont = font)
        updateTheme(newConfig)
    }
}