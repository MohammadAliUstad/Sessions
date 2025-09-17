package com.yugentech.sessions.dependencyInjection

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
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
import com.yugentech.sessions.notifications.active.ActiveService
import com.yugentech.sessions.notifications.scheduled.KoinWorkerFactory
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SessionsApp : Application() {

    companion object {
        private const val TAG = "SessionsApp"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "=== APPLICATION STARTING ===")

        FirebaseApp.initializeApp(this)

        Log.d(TAG, "Starting Koin...")
        startKoin {
            androidLogger()
            androidContext(this@SessionsApp)
            modules(
                authModule,
                databaseModule,
                sessionModule,
                userModule,
                themeModule,
                viewModelModule,
                alertsModule,
                timerModule,
                dataStoreModule,
                notificationModule
            )
        }
        Log.d(TAG, "Koin started successfully")

        // Initialize WorkManager manually
        initializeWorkManager()

        get<ActiveService>().createNotificationChannels()
        Log.d(TAG, "=== APPLICATION STARTED ===")
    }

    private fun initializeWorkManager() {
        Log.d(TAG, "🔧 Initializing WorkManager manually...")

        try {
            val factory = get<KoinWorkerFactory>()
            Log.d(TAG, "✅ KoinWorkerFactory obtained: $factory")

            val config = Configuration.Builder()
                .setWorkerFactory(factory)
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .build()

            WorkManager.initialize(this, config)
            Log.d(TAG, "🚀 WorkManager initialized successfully with custom factory")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to initialize WorkManager", e)
            throw e
        }
    }
}