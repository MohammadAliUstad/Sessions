package com.yugentech.sessions.dependencyInjection

import android.app.Application
import androidx.work.Configuration
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

class SessionsApp : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

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

        // Create notification channels early
        get<ActiveService>().createNotificationChannels()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(KoinWorkerFactory())
            .build()
}