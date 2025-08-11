package com.yugentech.sessions.models

import java.util.UUID

data class Session(
    val sessionId: String = UUID.randomUUID().toString(),
    val duration: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "sessionId" to sessionId,
        "duration" to duration,
        "timestamp" to timestamp
    )

    companion object {
        fun fromMap(map: Map<String, Any>): Session? {
            val sessionId = map["sessionId"] as? String
            val duration = (map["duration"] as? Number)?.toInt()
            val timestamp = map["timestamp"] as? Long

            return if (sessionId != null && duration != null && timestamp != null) {
                Session(sessionId, duration, timestamp)
            } else null
        }
    }
}