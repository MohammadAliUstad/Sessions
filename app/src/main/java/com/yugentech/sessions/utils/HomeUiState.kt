package com.yugentech.sessions.utils

import com.yugentech.sessions.theme.tokens.dimensions.AppConstants

data class HomeUiState(
    val isRunning: Boolean = false,
    val selectedDuration: Int = AppConstants.ZERO,
    val currentTime: Int = AppConstants.ZERO,
    val errorMessage: String? = null
)