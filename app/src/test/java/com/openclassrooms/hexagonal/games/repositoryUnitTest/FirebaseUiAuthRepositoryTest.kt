package com.openclassrooms.hexagonal.games.repositoryUnitTest

import android.content.Context
import app.cash.turbine.test
import com.firebase.ui.auth.AuthException
import com.firebase.ui.auth.AuthState
import com.firebase.ui.auth.FirebaseAuthUI
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.hexagonal.games.data.repository.FirebaseUiAuthRepository
import com.openclassrooms.hexagonal.games.domain.exception.DomainAuthException
import com.openclassrooms.hexagonal.games.domain.model.LocalAuthState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseUiAuthRepositoryTest {

    private val context: Context = mockk(relaxed = true)
    private val authUi: FirebaseAuthUI = mockk()
    private lateinit var repository: FirebaseUiAuthRepository

    @Before
    fun setup() {
        repository = FirebaseUiAuthRepository(context, authUi)
    }

    @Test
    fun `userLogState emits LoggedIn when firebase state is Success`() = runTest {
        val firebaseUser = mockk<FirebaseUser> {
            every { uid } returns "user123"
        }
        val authStateFlow = flowOf(AuthState.Success(
            result = null,
            user = firebaseUser,
            isNewUser = false
        ))
        every { authUi.authStateFlow() } returns authStateFlow

        repository.userLogState().test {
            val item = awaitItem()
            assertTrue(item is LocalAuthState.LoggedIn)
            assertEquals("user123", (item as LocalAuthState.LoggedIn).uid)
            awaitComplete()
        }
    }

    @Test
    fun `userLogState emits LoggedOut when firebase state is anonymous or other`() = runTest {
        //GIVEN
        val authStateFlow = flowOf(AuthState.Idle)
        every { authUi.authStateFlow() } returns authStateFlow

        //WHEN
        repository.userLogState().test {
            //THEN
            assertEquals(LocalAuthState.LoggedOut, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `signOut returns success when firebase succeeds`() = runTest {
        //GIVEN
        coEvery { authUi.signOut(context) } returns Unit

        //WHEN
        val result = repository.signOut()

        //THEN
        assertTrue(result.isSuccess)
    }

    @Test
    fun `signOut returns failure when firebase throws`() = runTest {
        //GIVEN
        val exception = Exception("Sign out failed")
        coEvery { authUi.signOut(context) } throws exception

        //WHEN
        val result = repository.signOut()

        //THEN
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `deleteAccount throws NeedsReauth when credentials are invalid`() = runTest {
        //GIVEN
        val firebaseException = mockk<AuthException.InvalidCredentialsException>()
        coEvery { authUi.delete(context) } throws firebaseException

        //WHEN
        val result = repository.deleteAccount()

        //THEN
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is DomainAuthException.NeedsReauth)
    }

    @Test
    fun `deleteAccount throws NetworkError on connection issue`() = runTest {
        //GIVEN
        val firebaseException = mockk<AuthException.NetworkException>()
        coEvery { authUi.delete(context) } throws firebaseException

        //WHEN
        val result = repository.deleteAccount()

        //THEN
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is DomainAuthException.NetworkError)
    }

    @Test
    fun `checkIfReauthIsNeeded returns true when last login is recent`() = runTest {
        //GIVEN
        val now = System.currentTimeMillis()
        val recentLogin = now - 60_000 // 1 minute ago

        val mockUser = mockk<FirebaseUser> {
            every { metadata?.lastSignInTimestamp } returns recentLogin
        }
        every { authUi.getCurrentUser() } returns mockUser

        //WHEN
        val result = repository.checkIfReauthIsNeeded()

        //THEN
        assertTrue(result) // (now - recent) < 300_000 est vrai
    }

    @Test
    fun `checkIfReauthIsNeeded returns false when last login is old`() = runTest {
        //GIVEN
        val now = System.currentTimeMillis()
        val oldLogin = now - 600_000 // 10 minutes ago

        val mockUser = mockk<FirebaseUser> {
            every { metadata?.lastSignInTimestamp } returns oldLogin
        }
        every { authUi.getCurrentUser() } returns mockUser

        //WHEN
        val result = repository.checkIfReauthIsNeeded()

        //THEN
        assertFalse(result)
    }

    @Test
    fun `getUserId returns uid when user is logged in`() {
        //GIVEN
        val mockAuth = mockk<com.google.firebase.auth.FirebaseAuth>()
        val mockUser = mockk<FirebaseUser>()

        every { authUi.auth } returns mockAuth
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "test_uid"

        //WHEN
        val result = repository.getUserId()

        //THEN
        assertEquals("test_uid", result)
    }


}