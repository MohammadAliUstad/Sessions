package com.yugentech.sessions.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.theme.models.ColorTheme
import com.yugentech.sessions.theme.models.ThemeConfiguration
import com.yugentech.sessions.theme.models.ThemeMode
import com.yugentech.sessions.theme.themeRepository.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val repository: ThemeRepository
) : ViewModel() {

    // Internal mutable state for immediate updates
    private val _themeConfiguration = MutableStateFlow(
        ThemeConfiguration(ThemeMode.LIGHT, ColorTheme.DYNAMIC, true)
    )

    // Expose immutable state to UI
    val themeConfiguration: StateFlow<ThemeConfiguration> = _themeConfiguration.asStateFlow()

    init {
        // Sync with repository on init and keep in sync
        viewModelScope.launch {
            repository.themeConfiguration.collect { config ->
                _themeConfiguration.value = config
            }
        }
    }

    // Updates immediately for instant UI response, then persists
    fun updateTheme(config: ThemeConfiguration) {
        // Optimistic update - UI changes instantly
        _themeConfiguration.value = config

        // Persist to DataStore in background
        viewModelScope.launch {
            repository.setThemeConfig(config)
        }
    }
}