package com.yugentech.sessions.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.authentication.authUtils.AuthResult
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.user.userRepository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        private const val TAG = "UserViewModel"
    }

    private val _userState = MutableStateFlow<UserData?>(null)
    val userState: StateFlow<UserData?> = _userState.asStateFlow()

    private val _totalTime = MutableStateFlow<Long>(0L)
    val totalTime: StateFlow<Long> = _totalTime.asStateFlow()

    private val _updateStatus = MutableStateFlow<UserResult<Unit>?>(null)
    val updateStatus: StateFlow<UserResult<Unit>?> = _updateStatus.asStateFlow()

    private val _syncStatus = MutableStateFlow<UserResult<Unit>?>(null)
    val syncStatus: StateFlow<UserResult<Unit>?> = _syncStatus.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var currentUserId: String? = null

    fun loadUser(userId: String) {
        if (currentUserId == userId) {
            Log.d(TAG, "Already observing user: $userId")
            return
        }

        Log.d(TAG, "Loading user data for: $userId")
        currentUserId = userId
        _isLoading.value = true
        _errorMessage.value = null

        userRepository.getUserFlow(userId)
            .onEach { userData ->
                Log.d(TAG, "User data updated: ${userData?.name ?: "null"}")
                _userState.value = userData
                _isLoading.value = false
                if (userData == null) {
                    _errorMessage.value = "User not found"
                } else {
                    _errorMessage.value = null
                }
            }
            .launchIn(viewModelScope)

        userRepository.getTotalTimeFlow(userId)
            .onEach { totalTime ->
                Log.d(TAG, "Total time updated: ${totalTime}s")
                _totalTime.value = totalTime
            }
            .launchIn(viewModelScope)
    }

    fun updateUser(userData: UserData) {
        Log.d(TAG, "Updating user: ${userData.name}")
        viewModelScope.launch {
            _updateStatus.value = UserResult.Loading
            _errorMessage.value = null

            when (val result = userRepository.updateUser(userData)) {
                is AuthResult.Success -> {
                    Log.d(TAG, "User updated successfully: ${userData.name}")
                    _updateStatus.value = UserResult.Success(Unit)
                    _errorMessage.value = null
                }
                is AuthResult.Error -> {
                    Log.e(TAG, "Failed to update user: ${result.message}")
                    _updateStatus.value = UserResult.Error("Failed to update user: ${result.message}")
                    _errorMessage.value = "Failed to update user: ${result.message}"
                }
            }
        }
    }

    fun addStudyTime(userId: String, additionalSeconds: Int) {
        Log.d(TAG, "Adding ${additionalSeconds}s study time to user: $userId")
        viewModelScope.launch {
            try {
                userRepository.addStudyTime(userId, additionalSeconds)
                Log.d(TAG, "Study time added successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add study time", e)
                _errorMessage.value = "Failed to add study time: ${e.message}"
            }
        }
    }

    fun syncUserToFirestore(userId: String) {
        Log.d(TAG, "Manual sync to Firestore for user: $userId")
        viewModelScope.launch {
            _syncStatus.value = UserResult.Loading

            try {
                userRepository.syncUserToFirestore(userId)
                Log.d(TAG, "Manual sync completed successfully")
                _syncStatus.value = UserResult.Success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Manual sync failed", e)
                _syncStatus.value = UserResult.Error("Sync failed: ${e.message}")
            }
        }
    }

    suspend fun getCurrentUserDirect(userId: String): UserData? {
        return try {
            val user = userRepository.getUser(userId)
            Log.d(TAG, "Direct user fetch: ${user?.name ?: "null"}")
            user
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get user directly", e)
            null
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearUpdateStatus() {
        _updateStatus.value = null
    }

    fun clearSyncStatus() {
        _syncStatus.value = null
    }

    fun getCurrentUser(): UserData? = _userState.value

    fun hasUser(): Boolean = _userState.value != null

    fun getFormattedTotalTime(): String {
        val totalSeconds = _totalTime.value
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun refreshUser(userId: String) {
        Log.d(TAG, "Force refreshing user: $userId")
        currentUserId = null
        loadUser(userId)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared")
        currentUserId = null
    }
}

sealed class UserResult<out T> {
    object Loading : UserResult<Nothing>()
    data class Success<T>(val data: T) : UserResult<T>()
    data class Error(val message: String, val exception: Throwable? = null) : UserResult<Nothing>()
}