package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.dependencyInjection.AppInitializer
import com.yugentech.sessions.dependencyInjection.NotificationInitializer
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val startModule = module {

    single {
        NotificationInitializer(
            get(),
            get()
        ) }

    single {
        AppInitializer(
            get()
        )
    }
}