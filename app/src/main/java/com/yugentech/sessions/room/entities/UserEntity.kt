package com.yugentech.sessions.room.entities

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yugentech.sessions.models.UserData

// Database table definition for storing user profiles
@Keep
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val name: String?,
    val email: String?,
    val avatarId: Int?
) {
    // Maps the database entity back to the domain model
    fun toUserData(): UserData {
        return UserData(
            userId = userId,
            name = name,
            email = email,
            avatarId = avatarId
        )
    }

    companion object {
        // Maps the domain model to a database entity
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