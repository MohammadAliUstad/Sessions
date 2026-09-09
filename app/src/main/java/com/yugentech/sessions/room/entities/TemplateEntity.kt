package com.yugentech.sessions.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yugentech.sessions.templates.model.Template
import com.yugentech.sessions.timer.config.TimerConfig

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val focusDuration: Int,
    val shortBreakDuration: Int,
    val longBreakDuration: Int,
    val targetSets: Int,
    val setsPerLongBreak: Int,
    val longBreakEnabled: Boolean,
    val activeBackgroundSoundId: String?,
    val isAmbientEnabled: Boolean
) {
    fun toTemplate() = Template(
        id = id,
        name = name,
        focusDuration = focusDuration,
        shortBreakDuration = shortBreakDuration,
        longBreakDuration = longBreakDuration,
        targetSets = targetSets,
        setsPerLongBreak = setsPerLongBreak,
        longBreakEnabled = longBreakEnabled,
        activeBackgroundSoundId = activeBackgroundSoundId,
        isAmbientEnabled = isAmbientEnabled
    )

    companion object {
        fun fromConfig(name: String, config: TimerConfig) = TemplateEntity(
            name = name,
            focusDuration = config.focusDuration,
            shortBreakDuration = config.shortBreakDuration,
            longBreakDuration = config.longBreakDuration,
            targetSets = config.targetSets,
            setsPerLongBreak = config.setsPerLongBreak,
            longBreakEnabled = config.longBreakEnabled,
            activeBackgroundSoundId = config.activeBackgroundSoundId,
            isAmbientEnabled = config.isAmbientEnabled
        )
    }
}
