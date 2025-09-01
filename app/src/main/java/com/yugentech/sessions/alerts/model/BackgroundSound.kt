package com.yugentech.sessions.alerts.models

import androidx.annotation.RawRes
import com.yugentech.sessions.R

// Enumeration mapping string IDs to raw audio resource files
enum class BackgroundSound(
    val id: String,
    @param:RawRes val resId: Int?
) {
    NONE("none", null),
    RAIN("rain", R.raw.rain),
    BROWN_NOISE("brown_noise", R.raw.brown_noise),
    FIREPLACE("fireplace", R.raw.fireplace),
    LIBRARY("library", R.raw.library),
    RIVERSIDE("riverside", R.raw.riverside);

    companion object {
        // Helper to find a sound by its ID, returning NONE if not found
        fun fromId(id: String?): BackgroundSound {
            return entries.find { it.id == id } ?: NONE
        }
    }
}