package com.yugentech.sessions.dependencyInjection.modules

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

// Extension delegates for creating separate DataStore files
private val Context.alertsDataStore: DataStore<Preferences> by preferencesDataStore(name = "alerts")
private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme")
private val Context.notificationDataStore: DataStore<Preferences> by preferencesDataStore(name = "notification")
private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

val dataStoreModule = module {

    // Provides the DataStore instance for Alert settings (Sound/Haptics)
    single<DataStore<Preferences>>(named("alerts")) {
        androidContext().alertsDataStore
    }

    // Provides the DataStore instance for Theme settings (Light/Dark mode)
    single<DataStore<Preferences>>(named("theme")) {
        androidContext().themeDataStore
    }

    // Provides the DataStore instance for Notification preferences
    single<DataStore<Preferences>>(named("notification")) {
        androidContext().notificationDataStore
    }

    single<DataStore<Preferences>>(named("user")) {
        androidContext().userDataStore
    }
}