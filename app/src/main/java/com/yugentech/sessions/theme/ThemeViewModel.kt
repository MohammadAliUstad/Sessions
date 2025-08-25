package com.yugentech.sessions.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.theme.themeRepository.ThemeRepository
import com.yugentech.sessions.theme.utils.ColorTheme
import com.yugentech.sessions.theme.utils.ThemeConfiguration
import com.yugentech.sessions.theme.utils.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    val themeConfiguration: StateFlow<ThemeConfiguration> = themeRepository.themeConfiguration
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ThemeConfiguration( // guaranteed first value
                themeMode = ThemeMode.LIGHT,
                colorTheme = ColorTheme.DYNAMIC,
                useDynamicColors = true
            )
        )

    /////

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                themeRepository.setThemeMode(themeMode)
            } catch (e: Exception) {
                _error.value = "Failed to update theme mode: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateColorTheme(colorTheme: ColorTheme) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                themeRepository.setColorTheme(colorTheme)
            } catch (e: Exception) {
                _error.value = "Failed to update color theme: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUseDynamicColors(useDynamicColors: Boolean) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                themeRepository.setUseDynamicColors(useDynamicColors)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}