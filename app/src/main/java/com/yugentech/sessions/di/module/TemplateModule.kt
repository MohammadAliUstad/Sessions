package com.yugentech.sessions.di.module

import com.yugentech.sessions.templates.repository.TemplateRepository
import com.yugentech.sessions.templates.repository.TemplateRepositoryImpl
import com.yugentech.sessions.templates.viewmodel.TemplateViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import timber.log.Timber

val templateModule = module {

    single<TemplateRepository> {
        Timber.v("Initializing TemplateRepository")
        TemplateRepositoryImpl(templateDao = get())
    }

    viewModel {
        Timber.v("Initializing TemplateViewModel")
        TemplateViewModel(templateRepository = get())
    }
}
