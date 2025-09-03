package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.alerts.HapticService
import com.yugentech.sessions.alerts.SoundService
import com.yugentech.sessions.alerts.alertsDatastore.AlertsManager
import com.yugentech.sessions.alerts.alertsDatastore.AlertsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val alertsModule = module {

    single {
        SoundService(androidContext())
    }

    single {
        HapticService(androidContext())
    }

    single {
        AlertsManager(
            dataStore = get(named("alerts"))
        )
    }

    single {
        AlertsRepository(
            alertsManager = get(),
            hapticService = get(),
            soundService = get()
        )
    }
}