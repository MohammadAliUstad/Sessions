package com.yugentech.sessions.announcements.repository

import com.yugentech.sessions.BuildConfig
import com.yugentech.sessions.announcements.catalog.AnnouncementsCatalog
import com.yugentech.sessions.announcements.datastore.AnnouncementDataStore
import com.yugentech.sessions.announcements.model.Announcement
import com.yugentech.sessions.user.datastore.UserDataStore
import kotlinx.coroutines.flow.first
import timber.log.Timber

/**
 * Resolves pending announcements for the running app version.
 * Handles fresh-install suppression and single-time presentation per release.
 */
interface AnnouncementRepository {
    /** Returns all un-seen announcements valid for the current build, ordered by priority. */
    suspend fun getPendingAnnouncements(): List<Announcement>

    /** Marks a single announcement as seen. */
    suspend fun markSeen(id: String)
}

class AnnouncementRepositoryImpl(
    private val announcementDataStore: AnnouncementDataStore,
    private val userDataStore: UserDataStore,
    private val catalog: List<Announcement> = AnnouncementsCatalog.ALL_ANNOUNCEMENTS,
    private val currentVersionCode: Int = BuildConfig.VERSION_CODE
) : AnnouncementRepository {

    override suspend fun getPendingAnnouncements(): List<Announcement> {
        val lastAckVersion = announcementDataStore.lastAcknowledgedVersion.first()
        val isOnboardingCompleted = userDataStore.isOnboardingCompleted.first()

        // Fresh install handling:
        // If the user has never completed onboarding and has no acknowledged version,
        // this is a brand new user install. Mark all current/past announcements as seen
        // so new users aren't flooded with "What's New" release notes.
        if (!isOnboardingCompleted && lastAckVersion == 0) {
            val allIds = catalog.map { it.id }
            announcementDataStore.markAllSeen(allIds, currentVersionCode)
            Timber.i("Fresh install detected: marked all announcements as seen")
            return emptyList()
        }

        val seenIds = announcementDataStore.seenAnnouncementIds.first()

        val pending = catalog
            .filter { announcement ->
                val matchesVersion = currentVersionCode in announcement.minVersionCode..announcement.maxVersionCode
                val notSeen = announcement.id !in seenIds
                matchesVersion && notSeen
            }
            .sortedByDescending { it.priority }

        // Update the acknowledged version so we know the user ran this build
        if (lastAckVersion != currentVersionCode) {
            announcementDataStore.updateLastAcknowledgedVersion(currentVersionCode)
        }

        return pending
    }

    override suspend fun markSeen(id: String) {
        announcementDataStore.markSeen(id)
    }
}
