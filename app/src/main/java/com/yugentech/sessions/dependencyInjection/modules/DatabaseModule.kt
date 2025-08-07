package com.yugentech.sessions.dependencyInjection.modules

import androidx.room.Room
import com.yugentech.sessions.room.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "sessions_database"
        ).build()
    }

    single {
        get<AppDatabase>().userDao()
    }

    single {
        get<AppDatabase>().sessionDao()
    }
}