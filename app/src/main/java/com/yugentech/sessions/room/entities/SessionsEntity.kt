package com.yugentech.sessions.room.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.yugentech.sessions.models.Session
import java.util.UUID

@Entity(
    tableName = "sessions",
    indices = [Index(value = ["userId"])]
)
data class SessionsEntity(
    @PrimaryKey
    val sessionId: String,
    val userId: String,
    val duration: Int,
    val timestamp: Long,
    val pendingSync: Boolean = true
) {
    fun toSession(): Session {
        return Session(
            duration = duration,
            timestamp = timestamp
        )
    }

    companion object {
        fun fromSession(
            session: Session,
            userId: String,
            sessionId: String = UUID.randomUUID().toString(),
            pendingSync: Boolean = true
        ): SessionsEntity {
            return SessionsEntity(
                sessionId = sessionId,
                userId = userId,
                duration = session.duration,
                timestamp = session.timestamp,
                pendingSync = pendingSync
            )
        }
    }
}