package com.yugentech.sessions

import android.app.PendingIntent
import android.content.Intent
import com.google.firebase.auth.FirebaseUser
import com.yugentech.sessions.authentication.AuthService
import com.yugentech.sessions.authentication.authRepository.AuthRepositoryImpl
import com.yugentech.sessions.authentication.authUtils.AuthResult
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

    // 1. Mock the Service (The wrapper around Firebase)
    // We relax it so we don't have to mock everything (like logs)
    private val authService = mockk<AuthService>(relaxed = true)

    // 2. The Class Under Test
    private val repository = AuthRepositoryImpl(authService)

    @Test
    fun `signIn delegates to service and returns success`() = runBlocking {
        // ARRANGE
        val email = "test@example.com"
        val password = "password123"
        val fakeUser = mockk<FirebaseUser>() // We can mock FirebaseUser classes!
        
        // When service.signIn is called, return Success
        coEvery { authService.signIn(email, password) } returns AuthResult.Success(fakeUser)

        // ACT
        val result = repository.signIn(email, password)

        // ASSERT
        // Verify we got success
        assertTrue(result is AuthResult.Success)
        assertEquals(fakeUser, (result as AuthResult.Success).data)

        // Verify the repository actually called the service
        coVerify(exactly = 1) { authService.signIn(email, password) }
    }

    @Test
    fun `signUp returns error when service fails`() = runBlocking {
        // ARRANGE
        val errorMsg = "Email already in use"
        coEvery { authService.signUp(any(), any(), any()) } returns AuthResult.Error(errorMsg)

        // ACT
        val result = repository.signUp("Name", "email@test.com", "pass")

        // ASSERT
        assertTrue(result is AuthResult.Error)
        assertEquals(errorMsg, (result as AuthResult.Error).message)
    }

    @Test
    fun `signOut calls service signOut`() {
        // ACT
        repository.signOut()

        // ASSERT
        // Since signOut is not suspend, we use verify (not coVerify)
        // We verify that the repository simply told the service to sign out
        io.mockk.verify(exactly = 1) { authService.signOut() }
    }

    @Test
    fun `getGoogleSignInIntent propagates pending intent from service`() = runBlocking {
        // ARRANGE
        val fakeIntent = mockk<PendingIntent>()
        val clientId = "web_client_id"
        
        coEvery { authService.getGoogleSignInIntent(clientId) } returns AuthResult.Success(fakeIntent)

        // ACT
        val result = repository.getGoogleSignInIntent(clientId)

        // ASSERT
        assertTrue(result is AuthResult.Success)
        assertEquals(fakeIntent, (result as AuthResult.Success).data)
    }

    @Test
    fun `authState observes flow from service`() = runBlocking {
        // 1. ARRANGE
        val fakeUser = mockk<FirebaseUser>()

        // Define the mock behavior FIRST
        every { authService.authStateFlow } returns flowOf(fakeUser)

        // 2. Create the repository AFTER the mock is ready
        // We cannot use the class-level 'repository' here because it was created too early.
        val freshRepository = AuthRepositoryImpl(authService)

        // 3. ACT
        var receivedUser: FirebaseUser? = null

        // Collect from the FRESH repository
        freshRepository.authState.collect {
            receivedUser = it
        }

        // 4. ASSERT
        assertEquals(fakeUser, receivedUser)
    }
}