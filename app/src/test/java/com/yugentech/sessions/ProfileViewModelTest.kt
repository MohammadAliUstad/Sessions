package com.yugentech.sessions

import com.yugentech.sessions.alerts.alertsDatastore.AlertsRepository
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.sessions.sessionsRepository.SessionsRepository
import com.yugentech.sessions.user.userRepository.UserRepository
import com.yugentech.sessions.viewModels.ProfileViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    // 1. Mocks: We fake the dependencies
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val sessionsRepository = mockk<SessionsRepository>(relaxed = true)
    private val alertsRepository = mockk<AlertsRepository>(relaxed = true)

    // The class under test
    private lateinit var viewModel: ProfileViewModel

    // Test Dispatcher for Coroutines
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        // Swap the Main dispatcher (UI thread) with our test dispatcher
        Dispatchers.setMain(testDispatcher)
        
        viewModel = ProfileViewModel(userRepository, sessionsRepository, alertsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUser updates uiState with user data when successful`() = runTest {
        // ARRANGE
        val fakeUser = UserData(userId = "123", name = "Test User")
        
        // When the repo is asked for a flow, return a flow containing our fake user
        every { userRepository.getUserFlow("123") } returns flowOf(fakeUser)

        // ACT
        viewModel.loadUser("123")

        // ASSERT
        // Check if the StateFlow now contains our user
        assertEquals(fakeUser, viewModel.uiState.value.user)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `upsertUser calls repository sync and updates saving state`() = runTest {
        // ARRANGE
        val newUser = UserData(userId = "123", name = "Updated Name")
        
        // ACT
        viewModel.upsertUser(newUser)

        // ASSERT
        // Verify that the viewmodel actually called the repository methods
        coVerify { userRepository.upsertUser(newUser) }
        coVerify { userRepository.syncUser(newUser) }
        
        // Verify saving is done (isSaving should be false at the end)
        assertEquals(false, viewModel.uiState.value.isSaving)
    }

    @Test
    fun `loadUser catches exceptions and updates errorMessage`() = runTest {
        // 1. ARRANGE
        // Simulate a crash/error in the Flow
        val exceptionMessage = "Network unavailable"
        every { userRepository.getUserFlow(any()) } returns flow {
            throw Exception(exceptionMessage)
        }

        // 2. ACT
        viewModel.loadUser("123")

        // 3. ASSERT
        // Did we catch the error and put it in the state?
        assertEquals(exceptionMessage, viewModel.uiState.value.errorMessage)

        // Did we remember to turn off the loading spinner?
        assertEquals(false, viewModel.uiState.value.isLoading)
    }
}