package com.yugentech.sessions.theme.config

import com.yugentech.sessions.R
import androidx.annotation.StringRes

// How the Home session card is painted
enum class CardStyle(@param:StringRes val label: Int) {
    FILLED(R.string.card_style_filled),
    OUTLINED(R.string.card_style_outlined),
    GLASS(R.string.card_style_glass)
}
