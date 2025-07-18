package com.yugentech.sessions.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.status.statusRepository.StatusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class StatusViewModel(
    private val repository: StatusRepository
) : ViewModel() {

    private val _statuses = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val statuses: StateFlow<Map<String, Boolean>> = _statuses.asStateFlow()

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
        viewModelScope.launch {
            repository.setStudyStatus(userId, isStudying)
        }
    }

    fun getUserStatusOnce(userId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.getStudyStatus(userId)
            onResult(result)
        }
    }
}