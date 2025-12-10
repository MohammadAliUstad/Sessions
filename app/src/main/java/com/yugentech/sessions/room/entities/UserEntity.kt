package com.yugentech.sessions.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yugentech.sessions.models.UserData

// Local database representation of a User
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val name: String?,
    val email: String?,
    val avatarId: Int?
) {
    // Converts local entity to domain model
    fun toUserData(): UserData {
        return UserData(
            userId = userId,
            name = name,
            email = email,
            avatarId = avatarId
        )
    }

    companion object {
        // Converts domain model to local entity
        fun fromUserData(userData: UserData): UserEntity {
            return UserEntity(
                userId = userData.userId,
                name = userData.name,
                email = userData.email,
                avatarId = userData.avatarId
            )
        }
    }
}