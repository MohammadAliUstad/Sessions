package com.yugentech.sessions.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yugentech.sessions.room.daos.SessionsDao
import com.yugentech.sessions.room.daos.UserDao
import com.yugentech.sessions.room.entities.SessionsEntity
import com.yugentech.sessions.room.entities.UserEntity

// Core Room database configuration defining tables and version
@Database(
    entities = [
        UserEntity::class,
        SessionsEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // Exposes the Data Access Object for user operations
    abstract fun userDao(): UserDao

    // Exposes the Data Access Object for session operations
    abstract fun sessionDao(): SessionsDao
}