package com.yugentech.sessions.auth.guest

import com.yugentech.sessions.room.daos.SessionsDao
import com.yugentech.sessions.room.daos.UserDao
import com.yugentech.sessions.templates.repository.TemplateRepository
import timber.log.Timber

/**
 * Clears this phone's data when someone leaves. A guest's sessions and templates live only on this
 * phone (nothing goes to Firestore), so leaving guest mode deletes them. A signed-in account's
 * synced sessions are a copy of the cloud's, so signing out drops them too.
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

    /**
     * Drops a signing-out account's synced sessions from this phone. They're a copy of the cloud's
     * and come back at the account's next sign-in (sign-out resets the one-time fetch). Without
     * this they'd stay under the account's ID, and since a session's ID is unique on the phone, a
     * guest importing this account's backup here would have every session skipped. Sessions not
     * yet uploaded stay, so nothing is lost; they sync when the account signs in again.
     */
    suspend fun clearSignedOutAccount(userId: String) {
        sessionsDao.deleteSyncedSessions(userId)
        Timber.i("Signed-out account's synced sessions removed from this phone")
    }
}
