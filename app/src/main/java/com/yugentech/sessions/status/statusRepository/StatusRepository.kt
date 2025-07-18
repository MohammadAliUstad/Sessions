package com.yugentech.sessions.status.statusRepository

import kotlinx.coroutines.flow.Flow

interface StatusRepository {
    suspend fun setStudyStatus(userId: String, isStudying: Boolean): Boolean
    suspend fun getStudyStatus(userId: String): Boolean
    fun getAllStatuses(): Flow<Map<String, Boolean>>
}