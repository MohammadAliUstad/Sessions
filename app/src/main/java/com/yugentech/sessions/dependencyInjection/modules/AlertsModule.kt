package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.alerts.HapticService
import com.yugentech.sessions.alerts.SoundService
import com.yugentech.sessions.alerts.alertsDatastore.AlertsPreferences
import com.yugentech.sessions.alerts.alertsRepository.AlertsRepository
import com.yugentech.sessions.alerts.BackgroundSoundService
import com.yugentech.sessions.alerts.alertsRepository.AlertsRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
        AlertsPreferences(
            dataStore = get(named("alerts"))
        )
    }

    // Repository coordinating hardware feedback based on user settings
    single<AlertsRepository> {
        AlertsRepositoryImpl(
            alertsPreferences = get(),
            hapticService = get(),
            soundService = get(),
            backgroundSoundService = get(),
            externalScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }
}