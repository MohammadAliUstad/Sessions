package com.yugentech.sessions

import com.yugentech.sessions.auth.repository.AuthRepository
import com.yugentech.sessions.room.daos.UserDao
import com.yugentech.sessions.room.entities.UserEntity
import com.yugentech.sessions.sessions.datastore.SyncDataStore
import com.yugentech.sessions.user.model.UserData
import com.yugentech.sessions.user.repository.UserRepositoryImpl
import com.yugentech.sessions.user.result.UserResult
import com.yugentech.sessions.user.service.UserService
import com.yugentech.sessions.utils.AppConstants
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UserRepositoryImplTest {

    private val userDao = mockk<UserDao>(relaxed = true)
    private val userService = mockk<UserService>()
    private val syncDataStore = mockk<SyncDataStore>(relaxed = true)
    private val authRepository = mockk<AuthRepository>(relaxed = true)
    private val repository = UserRepositoryImpl(userDao, userService, syncDataStore, authRepository)

    @Test
    fun `fetchUserOnce DOES NOTHING if preferences say user already fetched`() = runBlocking {
        every { syncDataStore.isUserFetchDone } returns flowOf(true)

        val result = repository.fetchUserOnce("123")

        assertTrue(result is UserResult.Success)
        
        coVerify(exactly = 0) { userService.fetchUser(any()) }
    }

    @Test
    fun `fetchUserOnce CALLS API if not fetched yet`() = runBlocking {
        val fakeUser = UserData(userId = "123", name = "Cloud User")
        
        every { syncDataStore.isUserFetchDone } returns flowOf(false)
        
        coEvery { userService.fetchUser("123") } returns UserResult.Success(fakeUser)

        val result = repository.fetchUserOnce("123")

        assertTrue(result is UserResult.Success)

        coVerify { userDao.saveUser(any<UserEntity>()) }
        
        coVerify { syncDataStore.setUserFetchDone(true) }
    }

    @Test
    fun `fetchUserOnce returns Error result when API fails`() = runBlocking {

        every { syncDataStore.isUserFetchDone } returns flowOf(false)

        coEvery { userService.fetchUser("123") } returns UserResult.Error("Server 500 Error")

        val result = repository.fetchUserOnce("123")

        assertTrue(result is UserResult.Error)
        assertEquals("Server 500 Error", (result as UserResult.Error).message)

        coVerify(exactly = 0) { userDao.saveUser(any()) }

        coVerify(exactly = 0) { syncDataStore.setUserFetchDone(true) }
    }

    @Test
    fun `fetchUserOnce returns Success without calling API for guest user`() = runBlocking {
        val result = repository.fetchUserOnce(AppConstants.GUEST_USER_ID)
        
        assertTrue(result is UserResult.Success)
        coVerify(exactly = 0) { userService.fetchUser(any()) }
    }

    @Test
    fun `syncUser returns Success without calling API for guest user`() = runBlocking {
        val result = repository.syncUser(UserData(userId = AppConstants.GUEST_USER_ID))
        
        assertTrue(result is UserResult.Success)
        coVerify(exactly = 0) { userService.uploadUser(any()) }
    }
}