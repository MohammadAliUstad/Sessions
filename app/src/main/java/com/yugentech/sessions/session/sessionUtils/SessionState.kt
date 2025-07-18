package com.yugentech.sessions.session.sessionUtils

import com.yugentech.sessions.models.Session

data class SessionState(
    val totalTime: Long = 0,
    val isTotalTimeLoaded: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null,
    val sessions: List<Session> = emptyList(),
    val isSessionsLoading: Boolean = false
)