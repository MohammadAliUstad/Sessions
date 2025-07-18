package com.yugentech.sessions.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.models.User
import com.yugentech.sessions.status.statusRepository.StatusRepository
import com.yugentech.sessions.user.userRepository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LeaderboardViewModel(
    private val userRepository: UserRepository,
    private val statusRepository: StatusRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val leaderboardUsers: StateFlow<List<User>> = _users.asStateFlow()

    fun getLeaderboard() {
        combine(
            userRepository.getAllUsers(),
            statusRepository.getAllStatuses()
        ) { users, statuses ->
            users.map { user ->
                user.copy(isStudying = statuses[user.userId] == true)
            }
        }.onEach { updatedList ->
            _users.value = updatedList
        }.launchIn(viewModelScope)
    }
}