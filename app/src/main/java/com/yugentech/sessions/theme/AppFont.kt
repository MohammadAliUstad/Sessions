package com.yugentech.sessions.theme

import androidx.annotation.FontRes
import com.yugentech.sessions.R

enum class AppFont(
    val id: String,
    val displayName: String,
    @param:FontRes val regularResId: Int
) {
    Google(
        id = "google",
        displayName = "Google Sans",
        regularResId = R.font.google_sans_flex
    ),
    Outfit(
        id = "outfit",
        displayName = "Outfit",
        regularResId = R.font.outfit
    ),
    Manrope(
        id = "manrope",
        displayName = "Manrope",
        regularResId = R.font.manrope
    ),
    Urbanist(
        id = "urbanist",
        displayName = "Urbanist",
        regularResId = R.font.urbanist
    ),
    Figtree(
        id = "figtree",
        displayName = "Figtree",
        regularResId = R.font.figtree
    ),
    Garamond(
        id = "garamond",
        displayName = "Garamond",
        regularResId = R.font.eb_garamond
    )
}