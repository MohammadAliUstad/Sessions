package com.yugentech.sessions.status.statusRepository

import com.yugentech.sessions.status.StatusService
import kotlinx.coroutines.flow.Flow

class StatusRepositoryImpl(
    private val service: StatusService
) : StatusRepository {

    override suspend fun setStudyStatus(userId: String, isStudying: Boolean): Boolean {
        return service.setStudyStatus(userId, isStudying)
    }

    override suspend fun getStudyStatus(userId: String): Boolean {
        return service.getStudyStatus(userId)
    }

    override fun getAllStatuses(): Flow<Map<String, Boolean>> {
        return service.getAllStatuses()
    }
}