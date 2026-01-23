package com.yugentech.sessions.dependencyInjection

import android.app.Application
import com.google.firebase.FirebaseApp
import com.yugentech.sessions.BuildConfig
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

class SessionsApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize logging: Logcat for Debug, Crashlytics for Release
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
        else
            Timber.plant(ReleaseTree())

        // Initialize Firebase services
        FirebaseApp.initializeApp(this)

        // Initialize Dependency Injection
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
    }
}