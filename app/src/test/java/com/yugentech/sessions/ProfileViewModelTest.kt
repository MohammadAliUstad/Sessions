package com.yugentech.sessions

import com.yugentech.sessions.alerts.repository.AlertsRepository
import com.yugentech.sessions.user.model.UserData
import com.yugentech.sessions.sessions.repository.SessionsRepository
import com.yugentech.sessions.user.repository.UserRepository
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

    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val sessionsRepository = mockk<SessionsRepository>(relaxed = true)
    private val alertsRepository = mockk<AlertsRepository>(relaxed = true)

    private lateinit var viewModel: ProfileViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        viewModel = ProfileViewModel(userRepository, sessionsRepository, alertsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUser updates uiState with user data when successful`() = runTest {
        val fakeUser = UserData(userId = "123", name = "Test User")
        
        every { userRepository.getUserFlow("123") } returns flowOf(fakeUser)

        viewModel.loadUser("123")

        assertEquals(fakeUser, viewModel.uiState.value.user)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `upsertUser calls repository sync and updates saving state`() = runTest {
        val newUser = UserData(userId = "123", name = "Updated Name")
        
        viewModel.upsertUser(newUser)

        coVerify { userRepository.upsertUser(newUser) }
        coVerify { userRepository.syncUser(newUser) }
        
        assertEquals(false, viewModel.uiState.value.isSaving)
    }

    @Test
    fun `loadUser catches exceptions and updates errorMessage`() = runTest {

        val exceptionMessage = "Network unavailable"
        every { userRepository.getUserFlow(any()) } returns flow {
            throw Exception(exceptionMessage)
        }

        viewModel.loadUser("123")


        assertEquals(exceptionMessage, viewModel.uiState.value.errorMessage)

        assertEquals(false, viewModel.uiState.value.isLoading)
    }
}