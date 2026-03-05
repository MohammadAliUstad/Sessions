package com.yugentech.sessions.theme.builder

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.yugentech.sessions.theme.AppFont

// Builds a variable FontFamily from the selected AppFont with all weight variants
@OptIn(ExperimentalTextApi::class)
fun getFontFamily(appFont: AppFont): FontFamily {
    return FontFamily(
        Font(
            resId = appFont.regularResId,
            variationSettings = FontVariation.Settings(FontVariation.weight(300)),
            weight = FontWeight.Light
        ),
        Font(
            resId = appFont.regularResId,
            variationSettings = FontVariation.Settings(FontVariation.weight(400)),
            weight = FontWeight.Normal
        ),
        Font(
            resId = appFont.regularResId,
            variationSettings = FontVariation.Settings(FontVariation.weight(500)),
            weight = FontWeight.Medium
        ),
        Font(
            resId = appFont.regularResId,
            variationSettings = FontVariation.Settings(FontVariation.weight(600)),
            weight = FontWeight.SemiBold
        ),
        Font(
            resId = appFont.regularResId,
            variationSettings = FontVariation.Settings(FontVariation.weight(700)),
            weight = FontWeight.Bold
        )
    )
}