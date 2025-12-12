package com.yugentech.sessions.models

import androidx.annotation.Keep
import java.util.UUID

// Represents a completed focus session with duration and timestamp
@Keep
data class Session(
    val sessionId: String = UUID.randomUUID().toString(),
    val duration: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
) {
    // Serializes session data for Firestore persistence
    fun toMap(): Map<String, Any> = mapOf(
        "sessionId" to sessionId,
        "duration" to duration,
        "timestamp" to timestamp
    )
}