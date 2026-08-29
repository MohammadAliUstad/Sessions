package com.yugentech.sessions.sessions.model

import androidx.annotation.Keep
import java.util.UUID

@Keep
data class Session(
    val sessionId: String = UUID.randomUUID().toString(),
    val duration: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sessionTask: String = "Focus Session"
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "sessionId" to sessionId,
        "duration" to duration,
        "timestamp" to timestamp,
        "sessionTask" to sessionTask
    )
}