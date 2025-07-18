package com.yugentech.sessions.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.status.statusRepository.StatusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class StatusViewModel(
    private val repository: StatusRepository
) : ViewModel() {
    private val _statuses = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    private var currentUserId: String? = null

    init {
        observeStatuses()
    }

    private fun observeStatuses() {
        repository.getAllStatuses()
            .onEach { statusMap ->
                _statuses.value = statusMap
            }
            .launchIn(viewModelScope)
    }

    fun setUserStatus(userId: String, isStudying: Boolean) {
        currentUserId = userId
        viewModelScope.launch {
            repository.setStudyStatus(userId, isStudying)
        }
    }

    fun cleanup() {
        viewModelScope.launch {
            currentUserId?.let { userId ->
                repository.setStudyStatus(userId, false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cleanup()
    }
}