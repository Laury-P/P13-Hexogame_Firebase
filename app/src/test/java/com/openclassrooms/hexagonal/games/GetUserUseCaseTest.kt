package com.openclassrooms.hexagonal.games

import com.google.firebase.firestore.util.Assert.fail
import com.openclassrooms.hexagonal.games.domain.exception.DomainAuthException
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.domain.repository.AuthRepository
import com.openclassrooms.hexagonal.games.domain.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.usecases.GetUserUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetUserUseCaseTest {

    private val authRepository: AuthRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private lateinit var getUserUseCase: GetUserUseCase

    @Before
    fun setup() {
        getUserUseCase = GetUserUseCase(authRepository, userRepository)
    }

    @Test
    fun `when userId is null should throw NeedsReauth exception`() = runTest {
        // GIVEN: L'utilisateur n'est pas connecté (ID null)
        coEvery { authRepository.getUserId() } returns null

        // WHEN & THEN: On vérifie que l'appel jette bien l'exception
        try {
            getUserUseCase()
            fail("Should have thrown DomainAuthException.NeedsReauth")
        } catch (e: DomainAuthException.NeedsReauth) {
            // Test réussi
        }
    }

    @Test
    fun `when userId exists should return user from repository`() = runTest {
        // GIVEN
        val mockId = "user_123"
        val mockUser = User(id = mockId, firstname = "Laura", lastname = "Morin")

        coEvery { authRepository.getUserId() } returns mockId
        coEvery { userRepository.getCurrentUser(mockId) } returns mockUser

        // WHEN
        val result = getUserUseCase()

        // THEN
        assertEquals(mockUser, result)
        // On vérifie que le repository utilisateur a bien été appelé avec le bon ID
        coVerify { userRepository.getCurrentUser(mockId) }
    }

    @Test
    fun `when userId exists but user not found in repository should return null`() = runTest {
        // GIVEN
        val mockId = "user_123"
        coEvery { authRepository.getUserId() } returns mockId
        coEvery { userRepository.getCurrentUser(mockId) } returns null

        // WHEN
        val result = getUserUseCase()

        // THEN
        assertNull(result)
    }
}