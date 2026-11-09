package com.yugentech.sessions.announcements.viewmodel

import com.yugentech.sessions.announcements.model.Announcement
import com.yugentech.sessions.announcements.model.AnnouncementStyle
import com.yugentech.sessions.announcements.repository.AnnouncementRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnnouncementViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: AnnouncementRepository = mockk(relaxed = true)

    private val sampleA = Announcement(
        id = "a",
        minVersionCode = 27,
        title = "A",
        message = "Msg A",
        style = AnnouncementStyle.DIALOG
    )
    private val sampleB = Announcement(
        id = "b",
        minVersionCode = 27,
        title = "B",
        message = "Msg B",
        style = AnnouncementStyle.MESSAGE
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `checkForAnnouncements marks first item seen immediately and exposes it`() = runTest {
        coEvery { repository.getPendingAnnouncements() } returns listOf(sampleA, sampleB)

        val vm = AnnouncementViewModel(repository)
        vm.checkForAnnouncements()
        advanceUntilIdle()

        val state = vm.state.value
        assertTrue(state.isInitialized)
        assertEquals(sampleA, state.current)
        assertEquals(listOf(sampleB), state.queue)
        coVerify(exactly = 1) { repository.markSeen("a") }
    }

    @Test
    fun `dismissCurrent advances through queue until empty`() = runTest {
        coEvery { repository.getPendingAnnouncements() } returns listOf(sampleA, sampleB)

        val vm = AnnouncementViewModel(repository)
        vm.checkForAnnouncements()
        advanceUntilIdle()

        // Dismiss first
        vm.dismissCurrent()
        advanceUntilIdle()

        assertEquals(sampleB, vm.state.value.current)
        assertTrue(vm.state.value.queue.isEmpty())
        coVerify(exactly = 1) { repository.markSeen("b") }

        // Dismiss second
        vm.dismissCurrent()
        advanceUntilIdle()

        assertNull(vm.state.value.current)
        assertTrue(vm.state.value.queue.isEmpty())
    }

    @Test
    fun `checkForAnnouncements is no-op if already initialized`() = runTest {
        coEvery { repository.getPendingAnnouncements() } returns listOf(sampleA)

        val vm = AnnouncementViewModel(repository)
        vm.checkForAnnouncements()
        advanceUntilIdle()

        vm.checkForAnnouncements()
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.getPendingAnnouncements() }
    }
}
