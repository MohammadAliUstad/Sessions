package com.yugentech.sessions.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

// Extension properties to define separate DataStore files for different features
private val Context.alertsDataStore: DataStore<Preferences> by preferencesDataStore(name = "alerts")
private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme")
private val Context.notificationDataStore: DataStore<Preferences> by preferencesDataStore(name = "notification")
private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
private val Context.timerDataStore: DataStore<Preferences> by preferencesDataStore(name = "timer")
private val Context.syncDataStore: DataStore<Preferences> by preferencesDataStore(name = "sync")


// Koin module providing named DataStore instances
val dataStoreModule = module {

    // DataStore for alert settings
    single<DataStore<Preferences>>(named("alerts")) {
        androidContext().alertsDataStore
    }

    // DataStore for app theme settings
    single<DataStore<Preferences>>(named("theme")) {
        androidContext().themeDataStore
    }

    // DataStore for notification settings
    single<DataStore<Preferences>>(named("notification")) {
        androidContext().notificationDataStore
    }

    // DataStore for user profile data
    single<DataStore<Preferences>>(named("user")) {
        androidContext().userDataStore
    }

    // DataStore for timer configuration
    single<DataStore<Preferences>>(named("timer")) {
        androidContext().timerDataStore
    }

    single<DataStore<Preferences>>(named("sync")) {
        androidContext().syncDataStore
    }
}