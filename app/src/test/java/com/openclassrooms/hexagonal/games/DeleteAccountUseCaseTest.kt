package com.openclassrooms.hexagonal.games

import com.openclassrooms.hexagonal.games.domain.exception.DomainAuthException
import com.openclassrooms.hexagonal.games.domain.repository.AuthRepository
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.usecases.DeleteAccountUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteAccountUseCaseTest {

    private val authRepository: AuthRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val postRepository: PostRepository = mockk()

    private lateinit var deleteAccountUseCase: DeleteAccountUseCase

    @Before
    fun setup() {
        deleteAccountUseCase = DeleteAccountUseCase(authRepository, userRepository, postRepository)
    }

    @Test
    fun `when reauth is needed should return NeedsReauth failure`() = runTest {
        // GIVEN
        coEvery { authRepository.getUserId() } returns "user_123"
        coEvery { authRepository.checkIfReauthIsNeeded() } returns true

        // WHEN
        val result = deleteAccountUseCase()

        // THEN
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is DomainAuthException.NeedsReauth)

        // On vérifie qu'on n'a pas essayé de supprimer les posts (sécurité)
        coVerify(exactly = 0) { postRepository.deleteAllPostsFromUser(any()) }
    }

    @Test
    fun `when post deletion fails should stop and return failure`() = runTest {
        // GIVEN
        val uid = "user_123"
        coEvery { authRepository.getUserId() } returns uid
        coEvery { authRepository.checkIfReauthIsNeeded() } returns false
        // Simulation d'un échec lors de la suppression des posts
        coEvery { postRepository.deleteAllPostsFromUser(uid) } returns Result.failure(Exception())

        // WHEN
        val result = deleteAccountUseCase()

        // THEN
        assertTrue(result.isFailure)
        assertEquals("Failed to delete posts", result.exceptionOrNull()?.message)

        // On vérifie que la suite n'a PAS été appelée
        coVerify(exactly = 0) { userRepository.deleteUser(any()) }
    }

    @Test
    fun `when everything succeeds should return success`() = runTest {
        // GIVEN
        val uid = "user_123"
        coEvery { authRepository.getUserId() } returns uid
        coEvery { authRepository.checkIfReauthIsNeeded() } returns false
        coEvery { postRepository.deleteAllPostsFromUser(uid) } returns Result.success(Unit)
        coEvery { postRepository.deleteAllCommentsFromUser(uid) } returns Result.success(Unit)
        coEvery { userRepository.deleteUser(uid) } returns Result.success(Unit)
        coEvery { authRepository.deleteAccount() } returns Result.success(Unit)

        // WHEN
        val result = deleteAccountUseCase()

        // THEN
        assertTrue(result.isSuccess)

        // On vérifie que TOUTE la chaîne a été parcourue
        coVerify { postRepository.deleteAllPostsFromUser(uid) }
        coVerify { postRepository.deleteAllCommentsFromUser(uid) }
        coVerify { userRepository.deleteUser(uid) }
        coVerify { authRepository.deleteAccount() }
    }
}