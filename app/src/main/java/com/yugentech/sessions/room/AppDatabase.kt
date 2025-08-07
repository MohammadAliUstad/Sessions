package com.yugentech.sessions.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun sessionDao(): SessionsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sessions_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}