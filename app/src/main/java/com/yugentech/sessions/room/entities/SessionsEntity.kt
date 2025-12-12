package com.yugentech.sessions.room.entities

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.yugentech.sessions.models.Session
import java.util.UUID

// Local database representation of a Session, indexed by userId for faster queries
@Keep
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
    // Converts local entity to domain model
    fun toSession(): Session {
        return Session(
            sessionId = sessionId, // Pass ID to ensure consistency
            duration = duration,
            timestamp = timestamp
        )
    }

    companion object {
        // Converts domain model to local entity
        fun fromSession(
            session: Session,
            userId: String,
            sessionId: String = session.sessionId.ifEmpty { UUID.randomUUID().toString() },
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