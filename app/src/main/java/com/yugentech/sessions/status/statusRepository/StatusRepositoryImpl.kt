package com.yugentech.sessions.status.statusRepository

import android.util.Log
import com.yugentech.sessions.status.StatusResult
import com.yugentech.sessions.status.StatusService
import kotlinx.coroutines.flow.Flow

class StatusRepositoryImpl(
    private val statusService: StatusService
) : StatusRepository {

    override suspend fun setStudyStatus(userId: String, isStudying: Boolean): StatusResult<Unit> {
        Log.e("StatusRepositoryImpl", "setStudyStatus called with userId: $userId, isStudying: $isStudying")
        return statusService.setStudyStatus(userId, isStudying)
    }

    override suspend fun getStudyStatus(userId: String): StatusResult<Boolean> {
        return statusService.getStudyStatus(userId)
    }

    override fun getAllStatuses(): Flow<Map<String, Boolean>> {
        return statusService.getAllStatuses()
    }
}