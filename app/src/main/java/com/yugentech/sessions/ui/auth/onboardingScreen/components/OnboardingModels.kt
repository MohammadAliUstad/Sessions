package com.yugentech.sessions.ui.auth.onboardingScreen.components

import androidx.compose.ui.graphics.vector.ImageVector

data class FeatureHighlight(
    val icon: ImageVector,
    val text: String
)

data class PageContent(
    val title: String,
    val description: String,
    val highlights: List<FeatureHighlight>,
    val imageRes: Int
)
