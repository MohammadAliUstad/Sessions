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

    private val userDao = mockk<UserDao>(relaxed = true)
    private val userService = mockk<UserService>()
    private val syncPreferences = mockk<SyncPreferences>(relaxed = true)
    private val repository = UserRepositoryImpl(userDao, userService, syncPreferences)

    @Test
    fun `fetchUserOnce DOES NOTHING if preferences say user already fetched`() = runBlocking {
        every { syncPreferences.isUserFetchDone() } returns flowOf(true)

        val result = repository.fetchUserOnce("123")

        assertTrue(result is UserResult.Success)
        
        coVerify(exactly = 0) { userService.fetchUser(any()) }
    }

    @Test
    fun `fetchUserOnce CALLS API if not fetched yet`() = runBlocking {
        val fakeUser = UserData(userId = "123", name = "Cloud User")
        
        every { syncPreferences.isUserFetchDone() } returns flowOf(false)
        
        coEvery { userService.fetchUser("123") } returns UserResult.Success(fakeUser)

        val result = repository.fetchUserOnce("123")

        assertTrue(result is UserResult.Success)

        coVerify { userDao.saveUser(any<UserEntity>()) }
        
        coVerify { syncPreferences.setUserFetchDone(true) }
    }

    @Test
    fun `fetchUserOnce returns Error result when API fails`() = runBlocking {

        every { syncPreferences.isUserFetchDone() } returns flowOf(false)

        coEvery { userService.fetchUser("123") } returns UserResult.Error("Server 500 Error")

        val result = repository.fetchUserOnce("123")

        assertTrue(result is UserResult.Error)
        assertEquals("Server 500 Error", (result as UserResult.Error).message)

        coVerify(exactly = 0) { userDao.saveUser(any()) }

        coVerify(exactly = 0) { syncPreferences.setUserFetchDone(true) }
    }
}