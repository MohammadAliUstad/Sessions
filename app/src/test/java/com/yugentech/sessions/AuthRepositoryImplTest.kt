package com.yugentech.sessions

import android.app.PendingIntent
import com.google.firebase.auth.FirebaseUser
import com.yugentech.sessions.auth.service.AuthService
import com.yugentech.sessions.auth.repository.AuthRepositoryImpl
import com.yugentech.sessions.auth.result.AuthResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryImplTest {

    private val authService = mockk<AuthService>(relaxed = true)

    private val repository = AuthRepositoryImpl(authService)

    @Test
    fun `signIn delegates to service and returns success`() = runBlocking {
        val email = "test@example.com"
        val password = "password123"
        val fakeUser = mockk<FirebaseUser>()

        coEvery { authService.signIn(email, password) } returns AuthResult.Success(fakeUser)

        val result = repository.signIn(email, password)

        assertTrue(result is AuthResult.Success)
        assertEquals(fakeUser, (result as AuthResult.Success).data)

        coVerify(exactly = 1) { authService.signIn(email, password) }
    }

    @Test
    fun `signUp returns error when service fails`() = runBlocking {
        val errorMsg = "Email already in use"
        coEvery { authService.signUp(any(), any(), any()) } returns AuthResult.Error(errorMsg)

        val result = repository.signUp("Name", "email@test.com", "pass")

        assertTrue(result is AuthResult.Error)
        assertEquals(errorMsg, (result as AuthResult.Error).message)
    }

    @Test
    fun `signOut calls service signOut`() {
        repository.signOut()

        io.mockk.verify(exactly = 1) { authService.signOut() }
    }

    @Test
    fun `getGoogleSignInIntent propagates pending intent from service`() = runBlocking {
        val fakeIntent = mockk<PendingIntent>()
        val clientId = "web_client_id"

        coEvery { authService.getGoogleSignInIntent(clientId) } returns AuthResult.Success(
            fakeIntent
        )

        val result = repository.getGoogleSignInIntent(clientId)

        assertTrue(result is AuthResult.Success)
        assertEquals(fakeIntent, (result as AuthResult.Success).data)
    }

    @Test
    fun `authState observes flow from service`() = runBlocking {
        val fakeUser = mockk<FirebaseUser>()

        every { authService.authStateFlow } returns flowOf(fakeUser)

        val freshRepository = AuthRepositoryImpl(authService)

        var receivedUser: FirebaseUser? = null

        freshRepository.authState.collect {
            receivedUser = it
        }

        assertEquals(fakeUser, receivedUser)
    }
}