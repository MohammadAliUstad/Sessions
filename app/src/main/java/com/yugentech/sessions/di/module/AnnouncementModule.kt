package com.yugentech.sessions.di.module

import com.yugentech.sessions.announcements.datastore.AnnouncementDataStore
import com.yugentech.sessions.announcements.repository.AnnouncementRepository
import com.yugentech.sessions.announcements.repository.AnnouncementRepositoryImpl
import com.yugentech.sessions.announcements.viewmodel.AnnouncementViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val announcementModule = module {

    single {
        AnnouncementDataStore(
            dataStore = get(named("announcements"))
        )
    }

    single<AnnouncementRepository> {
        AnnouncementRepositoryImpl(
            announcementDataStore = get(),
            userDataStore = get()
        )
    }

    viewModel {
        AnnouncementViewModel(
            repository = get()
        )
    }
}
