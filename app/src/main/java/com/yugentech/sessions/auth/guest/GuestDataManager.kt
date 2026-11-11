package com.yugentech.sessions.auth.guest

import com.yugentech.sessions.room.daos.SessionsDao
import com.yugentech.sessions.room.daos.UserDao
import com.yugentech.sessions.templates.repository.TemplateRepository
import timber.log.Timber

/**
 * Removes a guest's data. A guest is a Firebase anonymous account whose data lives only on this
 * phone (nothing goes to Firestore), so leaving guest mode clears it here.
 */
class GuestDataManager(
    private val sessionsDao: SessionsDao,
    private val userDao: UserDao,
    private val templateRepository: TemplateRepository
) {

    /** Removes the guest's sessions, profile and templates from this phone. */
    suspend fun deleteLocalData(guestId: String) {
        sessionsDao.deleteAllSessions(guestId)
        userDao.deleteUser(guestId)
        templateRepository.clearLocal()
        Timber.i("Guest local data deleted")
    }
}
