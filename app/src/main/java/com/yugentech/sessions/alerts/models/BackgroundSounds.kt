package com.yugentech.sessions.alerts.models

import androidx.annotation.RawRes
import com.yugentech.sessions.R

enum class BackgroundSound(
    val id: String,
    @RawRes val resId: Int?
) {
    NONE("none", null),
    RAIN("rain", R.raw.rain),
    LIBRARY("library", R.raw.library),
    FIREPLACE("fireplace", R.raw.fireplace);

    companion object {
        fun fromId(id: String?): BackgroundSound {
            return entries.find { it.id == id } ?: NONE
        }
    }
}