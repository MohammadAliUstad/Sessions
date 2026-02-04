package com.yugentech.sessions.theme.getters

import androidx.annotation.FontRes
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.yugentech.quill.R

enum class AppFont(
    val id: String,
    val displayName: String,
    @FontRes val regularResId: Int
) {
    Google("google", "Google Sans", R.font.google_sans_flex),
    Outfit("outfit", "Outfit", R.font.outfit),
    Manrope("manrope", "Manrope", R.font.manrope),
    Urbanist("urbanist", "Urbanist", R.font.urbanist),
    Figtree("figtree", "Figtree", R.font.figtree),
    Garamond("garamond", "Garamond", R.font.eb_garamond);

    @OptIn(ExperimentalTextApi::class)
    fun toFontFamily(): FontFamily {
        // Create a FontFamily using variable font settings for different weights
        return FontFamily(
            Font(
                resId = regularResId,
                variationSettings = FontVariation.Settings(FontVariation.weight(300)),
                weight = FontWeight.Light
            ),
            Font(
                resId = regularResId,
                variationSettings = FontVariation.Settings(FontVariation.weight(400)),
                weight = FontWeight.Normal
            ),
            Font(
                resId = regularResId,
                variationSettings = FontVariation.Settings(FontVariation.weight(500)),
                weight = FontWeight.Medium
            ),
            Font(
                resId = regularResId,
                variationSettings = FontVariation.Settings(FontVariation.weight(600)),
                weight = FontWeight.SemiBold
            ),
            Font(
                resId = regularResId,
                variationSettings = FontVariation.Settings(FontVariation.weight(700)),
                weight = FontWeight.Bold
            )
        )
    }
}