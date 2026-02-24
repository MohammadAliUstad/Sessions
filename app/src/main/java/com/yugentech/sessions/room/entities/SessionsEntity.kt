package com.yugentech.sessions.room.entities

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.yugentech.sessions.sessions.model.Session
import java.util.UUID

// Database table definition for storing session records, indexed by user for speed
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
    val pendingSync: Boolean = true,
    val sessionTask: String = "Focus Session"
) {
    // Maps the database entity back to the domain model
    fun toSession(): Session {
        return Session(
            sessionId = sessionId,
            duration = duration,
            timestamp = timestamp,
            sessionTask = sessionTask
        )
    }

    companion object {
        // Maps the domain model to a database entity, handling ID generation if needed
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
                pendingSync = pendingSync,
                sessionTask = session.sessionTask
            )
        }
    }
}