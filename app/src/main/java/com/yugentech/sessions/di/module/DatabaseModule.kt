package com.yugentech.sessions.di.module

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yugentech.sessions.room.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import timber.log.Timber

private val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `templates` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `focusDuration` INTEGER NOT NULL,
                `shortBreakDuration` INTEGER NOT NULL,
                `longBreakDuration` INTEGER NOT NULL,
                `targetSets` INTEGER NOT NULL,
                `setsPerLongBreak` INTEGER NOT NULL,
                `longBreakEnabled` INTEGER NOT NULL,
                `activeBackgroundSoundId` TEXT,
                `isAmbientEnabled` INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}

val databaseModule = module {

    single {
        Timber.d("Initializing Room Database")
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "sessions_database"
        ).addMigrations(MIGRATION_3_4).build()
    }

    single { get<AppDatabase>().userDao() }

    single { get<AppDatabase>().sessionDao() }

    single { get<AppDatabase>().templateDao() }
}