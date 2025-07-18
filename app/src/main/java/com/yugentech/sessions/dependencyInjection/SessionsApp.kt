package com.yugentech.sessions.dependencyInjection

import android.app.Application
import com.google.firebase.FirebaseApp
import com.yugentech.sessions.dependencyInjection.modules.authModule
import com.yugentech.sessions.dependencyInjection.modules.leaderboardModule
import com.yugentech.sessions.dependencyInjection.modules.sessionModule
import com.yugentech.sessions.dependencyInjection.modules.statusModule
import com.yugentech.sessions.dependencyInjection.modules.userModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class SessionsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        startKoin {
            androidLogger()
            androidContext(this@SessionsApp)

            modules(
                authModule,
                sessionModule,
                statusModule,
                userModule,
                leaderboardModule
            )
        }
    }
}