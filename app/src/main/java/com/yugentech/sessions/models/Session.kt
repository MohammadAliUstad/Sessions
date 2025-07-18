package com.yugentech.sessions.models

data class Session(
    val duration: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "duration" to duration,
        "timestamp" to timestamp
    )

    companion object {
        fun fromMap(map: Map<String, Any>): Session? {
            val duration = (map["duration"] as? Long)?.toInt()
            val timestamp = map["timestamp"] as? Long

            return if (duration != null && timestamp != null) {
                Session(duration, timestamp)
            } else null
        }
    }
}