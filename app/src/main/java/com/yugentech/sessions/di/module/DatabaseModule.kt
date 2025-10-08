package com.yugentech.sessions.di.module

import androidx.room.Room
import com.yugentech.sessions.room.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import timber.log.Timber

// Koin module for Room database and DAO dependencies
val databaseModule = module {

    // Creates the Room database instance, wiping data if schema changes
    single {
        Timber.d("Initializing Room Database")
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "sessions_database"
        ).fallbackToDestructiveMigration(true).build()
    }

    // Provides access to user-related database operations
    single {
        get<AppDatabase>().userDao()
    }

    // Provides access to session-related database operations
    single {
        get<AppDatabase>().sessionDao()
    }
}