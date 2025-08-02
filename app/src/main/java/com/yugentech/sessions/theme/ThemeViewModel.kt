package com.yugentech.sessions.theme

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.theme.themeRepository.ThemeRepository
import com.yugentech.sessions.ui.theme.ColorTheme
import com.yugentech.sessions.ui.theme.ThemeConfig
import com.yugentech.sessions.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    // Main theme configuration state
    val themeConfig: StateFlow<ThemeConfig> = themeRepository.themeConfig
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5_000),
            initialValue = ThemeConfig()
        )

    // Loading state for UI feedback
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state for error handling
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Update the theme mode (Light, Dark, System)
     */
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
            } catch (e: Exception) {
                _error.value = "Failed to update dynamic colors setting: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}