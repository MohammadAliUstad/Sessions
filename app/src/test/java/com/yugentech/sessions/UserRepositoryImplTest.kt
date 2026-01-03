package com.yugentech.sessions

import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.room.daos.UserDao
import com.yugentech.sessions.room.entities.UserEntity
import com.yugentech.sessions.sessions.SyncPreferences
import com.yugentech.sessions.user.UserResult
import com.yugentech.sessions.user.UserService
import com.yugentech.sessions.user.userRepository.UserRepositoryImpl
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UserRepositoryImplTest {

    // Mocks
    private val userDao = mockk<UserDao>(relaxed = true)
    private val userService = mockk<UserService>()
    private val syncPreferences = mockk<SyncPreferences>(relaxed = true)

    // Class under test
    private val repository = UserRepositoryImpl(userDao, userService, syncPreferences)

    @Test
    fun `fetchUserOnce DOES NOTHING if preferences say user already fetched`() = runBlocking {
        // ARRANGE
        // Simulate that we have already fetched the user
        every { syncPreferences.isUserFetchDone() } returns flowOf(true)

        // ACT
        val result = repository.fetchUserOnce("123")

        // ASSERT
        // Should return Success
        assertTrue(result is UserResult.Success)
        
        // Verify we NEVER called the API
        coVerify(exactly = 0) { userService.fetchUser(any()) }
    }

    @Test
    fun `fetchUserOnce CALLS API if not fetched yet`() = runBlocking {
        // ARRANGE
        val fakeUser = UserData(userId = "123", name = "Cloud User")
        
        // 1. Prefs say "false" (not fetched yet)
        every { syncPreferences.isUserFetchDone() } returns flowOf(false)
        
        // 2. API returns success
        coEvery { userService.fetchUser("123") } returns UserResult.Success(fakeUser)

        // ACT
        val result = repository.fetchUserOnce("123")

        // ASSERT
        assertTrue(result is UserResult.Success)

        // Verify we saved to DB
        coVerify { userDao.saveUser(any<UserEntity>()) }
        
        // Verify we updated preferences
        coVerify { syncPreferences.setUserFetchDone(true) }
    }

    @Test
    fun `fetchUserOnce returns Error result when API fails`() = runBlocking {
        // 1. ARRANGE
        // Prefs say we need to fetch
        every { syncPreferences.isUserFetchDone() } returns flowOf(false)

        // But the API fails
        coEvery { userService.fetchUser("123") } returns UserResult.Error("Server 500 Error")

        // 2. ACT
        val result = repository.fetchUserOnce("123")

        // 3. ASSERT
        // Verify return type is Error
        assertTrue(result is UserResult.Error)
        assertEquals("Server 500 Error", (result as UserResult.Error).message)

        // CRITICAL: Ensure we did NOT save bad data to the local DB
        coVerify(exactly = 0) { userDao.saveUser(any()) }

        // CRITICAL: Ensure we did NOT mark the fetch as "Done" (so we try again next time)
        coVerify(exactly = 0) { syncPreferences.setUserFetchDone(true) }
    }
}