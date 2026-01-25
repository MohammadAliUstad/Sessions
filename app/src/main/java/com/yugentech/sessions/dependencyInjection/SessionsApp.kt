package com.yugentech.sessions.dependencyInjection

import android.app.Application
import com.google.firebase.FirebaseApp
import com.yugentech.sessions.dependencyInjection.modules.alertsModule
import com.yugentech.sessions.dependencyInjection.modules.authModule
import com.yugentech.sessions.dependencyInjection.modules.dataStoreModule
import com.yugentech.sessions.dependencyInjection.modules.databaseModule
import com.yugentech.sessions.dependencyInjection.modules.notificationModule
import com.yugentech.sessions.dependencyInjection.modules.sessionModule
import com.yugentech.sessions.dependencyInjection.modules.themeModule
import com.yugentech.sessions.dependencyInjection.modules.timerModule
import com.yugentech.sessions.dependencyInjection.modules.userModule
import com.yugentech.sessions.dependencyInjection.modules.viewModelModule
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
        Timber.plant(ReleaseTree())

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