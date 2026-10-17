package com.yugentech.sessions.di.module

import com.yugentech.sessions.templates.repository.TemplateRepository
import com.yugentech.sessions.templates.repository.TemplateRepositoryImpl
import com.yugentech.sessions.templates.service.TemplateService
import com.yugentech.sessions.templates.viewmodel.TemplateViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import timber.log.Timber

val templateModule = module {

    single {
        TemplateService(firestore = get())
    }

    single<TemplateRepository> {
        Timber.v("Initializing TemplateRepository")
        TemplateRepositoryImpl(
            templateDao = get(),
            templateService = get(),
            authRepository = get()
        )
    }

    viewModel {
        Timber.v("Initializing TemplateViewModel")
        TemplateViewModel(templateRepository = get())
    }
}
