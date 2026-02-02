package com.yugentech.sessions.di

import android.app.Application
import com.google.firebase.FirebaseApp
import com.yugentech.sessions.di.module.alertsModule
import com.yugentech.sessions.di.module.authModule
import com.yugentech.sessions.di.module.dataStoreModule
import com.yugentech.sessions.di.module.databaseModule
import com.yugentech.sessions.di.module.notificationModule
import com.yugentech.sessions.di.module.sessionModule
import com.yugentech.sessions.di.module.themeModule
import com.yugentech.sessions.di.module.timerModule
import com.yugentech.sessions.di.module.userModule
import com.yugentech.sessions.di.module.viewModelModule
import com.yugentech.sessions.utils.ReleaseTree
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

// Main Application class responsible for global initialization
class SessionsApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Configure logging: DebugTree for development, ReleaseTree for production
        Timber.Forest.plant(ReleaseTree())

        // Initialize Firebase SDK
        FirebaseApp.initializeApp(this)

        // Start Koin dependency injection and load all modules
        startKoin {
            androidLogger()
            androidContext(this@SessionsApp)
            modules(
                dataStoreModule,
                authModule,
                databaseModule,
                sessionModule,
                userModule,
                themeModule,
                viewModelModule,
                alertsModule,
                timerModule,
                notificationModule
            )
        }
    }
}