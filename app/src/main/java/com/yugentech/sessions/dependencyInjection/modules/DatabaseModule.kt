package com.yugentech.sessions.dependencyInjection.modules

import androidx.room.Room
import com.yugentech.sessions.room.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import timber.log.Timber

val databaseModule = module {

    // Builds the Room database instance with destructive migration fallback
    single {
        Timber.d("Initializing Room Database")
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "sessions_database"
        ).fallbackToDestructiveMigration(true).build()
    }

    // Provides the UserDao
    single {
        get<AppDatabase>().userDao()
    }

    // Provides the SessionDao
    single {
        get<AppDatabase>().sessionDao()
    }
}