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
}