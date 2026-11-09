package com.yugentech.sessions.announcements.repository

import com.yugentech.sessions.announcements.datastore.AnnouncementDataStore
import com.yugentech.sessions.announcements.model.Announcement
import com.yugentech.sessions.announcements.model.AnnouncementStyle
import com.yugentech.sessions.user.datastore.UserDataStore
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AnnouncementRepositoryTest {

    private val announcementDataStore: AnnouncementDataStore = mockk(relaxed = true)
    private val userDataStore: UserDataStore = mockk(relaxed = true)

    private val testCatalog = listOf(
        Announcement(
            id = "a1_v27_high",
            minVersionCode = 27,
            maxVersionCode = 27,
            priority = 100,
            style = AnnouncementStyle.DIALOG,
            title = "A1",
            message = "Msg 1"
        ),
        Announcement(
            id = "a2_v27_low",
            minVersionCode = 27,
            maxVersionCode = 27,
            priority = 10,
            style = AnnouncementStyle.MESSAGE,
            title = "A2",
            message = "Msg 2"
        ),
        Announcement(
            id = "a3_v26_old",
            minVersionCode = 26,
            maxVersionCode = 26,
            priority = 50,
            style = AnnouncementStyle.DIALOG,
            title = "Old",
            message = "Old Msg"
        )
    )

    @Test
    fun `fresh install suppresses all announcements and marks them seen`() = runTest {
        coEvery { announcementDataStore.lastAcknowledgedVersion } returns flowOf(0)
        coEvery { userDataStore.isOnboardingCompleted } returns flowOf(false)

        val repo = AnnouncementRepositoryImpl(
            announcementDataStore = announcementDataStore,
            userDataStore = userDataStore,
            catalog = testCatalog,
            currentVersionCode = 27
        )

        val pending = repo.getPendingAnnouncements()

        assertTrue(pending.isEmpty())
        coVerify(exactly = 1) {
            announcementDataStore.markAllSeen(listOf("a1_v27_high", "a2_v27_low", "a3_v26_old"), 27)
        }
    }

    @Test
    fun `upgrading existing user receives unseen announcements matching current version in priority order`() = runTest {
        // User had previously run version 25 and completed onboarding
        coEvery { announcementDataStore.lastAcknowledgedVersion } returns flowOf(25)
        coEvery { userDataStore.isOnboardingCompleted } returns flowOf(true)
        coEvery { announcementDataStore.seenAnnouncementIds } returns flowOf(emptySet())

        val repo = AnnouncementRepositoryImpl(
            announcementDataStore = announcementDataStore,
            userDataStore = userDataStore,
            catalog = testCatalog,
            currentVersionCode = 27
        )

        val pending = repo.getPendingAnnouncements()

        assertEquals(2, pending.size)
        assertEquals("a1_v27_high", pending[0].id)
        assertEquals("a2_v27_low", pending[1].id)
        coVerify { announcementDataStore.updateLastAcknowledgedVersion(27) }
    }

    @Test
    fun `already seen announcement is excluded`() = runTest {
        coEvery { announcementDataStore.lastAcknowledgedVersion } returns flowOf(27)
        coEvery { userDataStore.isOnboardingCompleted } returns flowOf(true)
        coEvery { announcementDataStore.seenAnnouncementIds } returns flowOf(setOf("a1_v27_high"))

        val repo = AnnouncementRepositoryImpl(
            announcementDataStore = announcementDataStore,
            userDataStore = userDataStore,
            catalog = testCatalog,
            currentVersionCode = 27
        )

        val pending = repo.getPendingAnnouncements()

        assertEquals(1, pending.size)
        assertEquals("a2_v27_low", pending[0].id)
    }

    @Test
    fun `markSeen delegates to dataStore`() = runTest {
        val repo = AnnouncementRepositoryImpl(
            announcementDataStore = announcementDataStore,
            userDataStore = userDataStore,
            catalog = testCatalog,
            currentVersionCode = 27
        )

        repo.markSeen("some_id")

        coVerify(exactly = 1) { announcementDataStore.markSeen("some_id") }
    }
}
