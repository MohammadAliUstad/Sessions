package com.yugentech.sessions.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yugentech.sessions.models.UserData

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val name: String?,
    val email: String?,
    val avatarId: Int,
    val totalTimeStudied: Long,
    val lastSyncTimestamp: Long,
    val pendingSync: Boolean
) {
    fun toUserData(): UserData {
        return UserData(
            userId = userId,
            name = name,
            email = email,
            avatarId = avatarId,
            totalTimeStudied = totalTimeStudied,
            lastSyncTimestamp = lastSyncTimestamp,
            pendingSync = pendingSync
        )
    }

    companion object {
        fun fromUserData(userData: UserData): UserEntity {
            return UserEntity(
                userId = userData.userId,
                name = userData.name,
                email = userData.email,
                avatarId = userData.avatarId,
                totalTimeStudied = userData.totalTimeStudied,
                lastSyncTimestamp = userData.lastSyncTimestamp,
                pendingSync = userData.pendingSync
            )
        }
    }
}