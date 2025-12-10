package com.yugentech.sessions.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.theme.models.ColorTheme
import com.yugentech.sessions.theme.models.ThemeConfiguration
import com.yugentech.sessions.theme.models.ThemeMode
import com.yugentech.sessions.theme.themeRepository.ThemeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val repository: ThemeRepository
) : ViewModel() {

    // Exposes the current theme state to the UI, with a default initial value
    val themeConfiguration: StateFlow<ThemeConfiguration> =
        repository.themeConfiguration.stateIn(
            viewModelScope, SharingStarted.Eagerly,
            ThemeConfiguration(ThemeMode.LIGHT, ColorTheme.DYNAMIC, true)
        )

    // Updates the global theme configuration
    fun updateTheme(config: ThemeConfiguration) = viewModelScope.launch {
        repository.setThemeConfig(config)
    }
}