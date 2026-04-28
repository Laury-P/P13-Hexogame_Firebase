package com.openclassrooms.hexagonal.games.repositoryUnitTest

import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.openclassrooms.hexagonal.games.data.repository.FirebaseNotificationRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseNotificationRepositoryTest {

    private val firebaseMessaging: FirebaseMessaging = mockk()
    private lateinit var repository: FirebaseNotificationRepository

    @Before
    fun setup() {
        repository = FirebaseNotificationRepository(firebaseMessaging)
    }

    @Test
    fun `subscribeToAllNotification should return success when firebase succeeds`() = runTest {
        // GIVEN: On prépare une Task simulée qui réussit
        val mockTask = mockk<Task<Void>> {
            every { isComplete } returns true
            every { isSuccessful } returns true
            every { isCanceled } returns false
            every { exception } returns null
            every { result } returns null
        }
        every { firebaseMessaging.subscribeToTopic("all") } returns mockTask

        // WHEN
        val result = repository.subscribeToAllNotification()

        // THEN
        assertTrue(result.isSuccess)
        verify { firebaseMessaging.subscribeToTopic("all") }
    }

    @Test
    fun `subscribeToAllNotification should return failure when firebase fails`() = runTest {
        // GIVEN : On crée une Task qui a échoué
        val firebaseException = Exception("No internet connection")
        val mockTask = mockk<Task<Void>> {
            every { isComplete } returns true
            every { isSuccessful } returns false
            every { isCanceled } returns false
            every { exception } returns firebaseException
            every { result } throws firebaseException
        }

        every { firebaseMessaging.subscribeToTopic("all") } returns mockTask

        // WHEN
        val result = repository.subscribeToAllNotification()

        // THEN
        assertTrue(result.isFailure)
        assertEquals(firebaseException, result.exceptionOrNull())
    }

    @Test
    fun `unsubscribeFromAllNotification should return success when firebase succeeds`() = runTest {
        // GIVEN
        val mockTask = mockk<Task<Void>>{
            every { isComplete } returns true
            every { isSuccessful } returns true
            every { isCanceled } returns false
            every { exception } returns null
            every { result } returns null
        }

        every { firebaseMessaging.unsubscribeFromTopic("all") } returns mockTask

        // WHEN
        val result = repository.unsubscribeFromAllNotification()

        // THEN
        assertTrue(result.isSuccess)
        verify { firebaseMessaging.unsubscribeFromTopic("all") }
    }

    @Test
    fun `unsubscribeFromAllNotification should return failure when topic is invalid`() = runTest {
        // GIVEN
        val firebaseException = Exception("Invalid topic name")
        val mockTask = mockk<Task<Void>> {
            every { isComplete } returns true
            every { isSuccessful } returns false
            every { isCanceled } returns false
            every { exception } returns firebaseException
            every { result } throws firebaseException
        }

        every { firebaseMessaging.unsubscribeFromTopic("all") } returns mockTask

        // WHEN
        val result = repository.unsubscribeFromAllNotification()

        // THEN
        assertTrue(result.isFailure)
        assertEquals("Invalid topic name", result.exceptionOrNull()?.message)
    }
}