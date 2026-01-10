package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.alerts.HapticService
import com.yugentech.sessions.alerts.SoundService
import com.yugentech.sessions.alerts.alertsDatastore.AlertsManager
import com.yugentech.sessions.alerts.alertsDatastore.AlertsRepository
import com.yugentech.sessions.alerts.alertsDatastore.backgroundSounds.BackgroundSoundService
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val alertsModule = module {

    // Service for playing audio alerts
    single {
        SoundService(androidContext())
    }

    // Service for performing haptic feedback
    single {
        HapticService(androidContext())
    }

    single {
        BackgroundSoundService(androidContext())
    }

    // Manages persistence of alert preferences
    single {
        AlertsManager(
            dataStore = get(named("alerts"))
        )
    }

    // Repository coordinating hardware feedback based on user settings
    single {
        AlertsRepository(
            alertsManager = get(),
            hapticService = get(),
            soundService = get(),
            backgroundSoundService = get()
        )
    }
}