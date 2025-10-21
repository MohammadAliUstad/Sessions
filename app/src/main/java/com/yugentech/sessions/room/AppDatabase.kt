package com.yugentech.sessions.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yugentech.sessions.room.daos.SessionsDao
import com.yugentech.sessions.room.daos.UserDao
import com.yugentech.sessions.room.entities.SessionsEntity
import com.yugentech.sessions.room.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        SessionsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun sessionDao(): SessionsDao
}