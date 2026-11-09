package com.yugentech.sessions.theme.config

import com.yugentech.sessions.R
import androidx.annotation.StringRes

// Which design the Home screen is drawn in, in the order Appearance offers them
enum class HomeLayout(@param:StringRes val label: Int, @param:StringRes val description: Int) {
    CLASSIC(R.string.layout_classic, R.string.layout_classic_description),
    REFINED(R.string.layout_refined, R.string.layout_refined_description),
    STUDIO(R.string.layout_studio, R.string.layout_studio_description)
}
