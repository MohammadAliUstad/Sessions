package com.yugentech.sessions.dependencyInjection.modules

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yugentech.sessions.dependencyInjection.NotificationPrefsDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val Context.alertsDataStore:
        DataStore<Preferences> by preferencesDataStore(name = "alerts")

private val Context.themeDataStore:
        DataStore<Preferences> by preferencesDataStore(name = "theme")

private val Context.notificationDataStore:
        DataStore<Preferences> by preferencesDataStore(name = "notification")


val dataStoreModule = module {

    single<DataStore<Preferences>>(named("alerts")) {
        androidContext().alertsDataStore
    }

    single<DataStore<Preferences>>(named("theme")) {
        androidContext().themeDataStore
    }

    single<DataStore<Preferences>>(named("notification")) {
        androidContext().notificationDataStore
    }
}