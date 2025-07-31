package com.yugentech.sessions.status.statusRepository

import com.yugentech.sessions.status.StatusResult
import kotlinx.coroutines.flow.Flow

interface StatusRepository {
    suspend fun setStudyStatus(userId: String, isStudying: Boolean): StatusResult<Unit>
    suspend fun getStudyStatus(userId: String): StatusResult<Boolean>
    fun getAllStatuses(): Flow<Map<String, Boolean>>
}