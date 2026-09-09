package com.yugentech.sessions.templates.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.templates.model.Template
import com.yugentech.sessions.templates.repository.TemplateRepository
import com.yugentech.sessions.timer.config.TimerConfig
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class TemplateViewModel(
    private val templateRepository: TemplateRepository
) : ViewModel() {

    val templates: StateFlow<List<Template>> = templateRepository.getAllTemplates()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    fun saveTemplate(name: String, config: TimerConfig) {
        if (name.isBlank()) return
        viewModelScope.launch {
            templateRepository.saveFromConfig(name.trim(), config)
            Timber.d("Template saved: ${name.trim()}")
        }
    }

    fun deleteTemplate(id: Long) {
        viewModelScope.launch {
            templateRepository.delete(id)
        }
    }
}
