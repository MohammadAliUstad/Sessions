package com.yugentech.sessions.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.theme.themeRepository.ThemeRepository
import com.yugentech.sessions.theme.utils.ColorTheme
import com.yugentech.sessions.theme.utils.ThemeConfiguration
import com.yugentech.sessions.theme.utils.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val repository: ThemeRepository
) : ViewModel() {

    val themeConfiguration: StateFlow<ThemeConfiguration> =
        repository.themeConfiguration.stateIn(
            viewModelScope, SharingStarted.Eagerly,
            ThemeConfiguration(ThemeMode.LIGHT, ColorTheme.DYNAMIC, true)
        )

    fun updateTheme(config: ThemeConfiguration) = viewModelScope.launch {
        repository.setThemeConfig(config)
    }

    fun resetToDefaults() = viewModelScope.launch {
        repository.resetThemeToDefaults()
    }
}