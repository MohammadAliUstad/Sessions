package com.yugentech.sessions.status

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.notification.SessionCleanupService
import com.yugentech.sessions.status.statusRepository.StatusRepository
import kotlinx.coroutines.launch

class StatusViewModel(
    private val statusRepository: StatusRepository
) : ViewModel() {

    fun setUserStudyStatus(userId: String, isStudying: Boolean) {
        viewModelScope.launch {
            statusRepository.setStudyStatus(userId, isStudying)
        }
    }

    fun sessionTrackingService(context: Context, userId: String, isStudying: Boolean) {

        val serviceIntent = Intent(context, SessionCleanupService::class.java).apply {
            putExtra("USER_ID", userId)
        }

        if (isStudying) {
            context.startService(serviceIntent)
        } else {
            context.stopService(serviceIntent)
        }
    }

    fun cleanupOnAppExit(context: Context, userId: String) {

        val serviceIntent = Intent(context, SessionCleanupService::class.java)
        context.stopService(serviceIntent)
        setUserStudyStatus(userId, false)
    }
}