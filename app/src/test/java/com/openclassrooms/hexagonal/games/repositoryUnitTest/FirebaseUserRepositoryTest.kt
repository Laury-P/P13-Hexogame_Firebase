package com.openclassrooms.hexagonal.games.repositoryUnitTest

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.data.repository.FirebaseUserRepository
import com.openclassrooms.hexagonal.games.domain.model.User
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class FirebaseUserRepositoryTest {

    private val firestore: FirebaseFirestore = mockk()
    private val mockCollection = mockk<CollectionReference>()
    private val mockDocumentRef = mockk<DocumentReference>()

    private lateinit var repository: FirebaseUserRepository

    @Before
    fun setup() {
        repository = FirebaseUserRepository(firestore)

        every { firestore.collection("users") } returns mockCollection
        every { mockCollection.document(any()) } returns mockDocumentRef
    }

    @Test
    fun `getCurrentUser should return user when firebase succeeds and user exists`() = runTest {
        // GIVEN
        val mockSnapshot = mockk<DocumentSnapshot>()
        val expectedUser = User(id = "123", firstname = "John", lastname = "Doe")

        every { mockDocumentRef.get() } returns mockSuccessfulTask(mockSnapshot)
        every { mockSnapshot.toObject(User::class.java) } returns expectedUser

        // WHEN
        val result = repository.getCurrentUser("123")

        // THEN
        assertEquals(expectedUser, result)
    }

    @Test
    fun `getCurrentUser should return null when firebase succeeds but user does not exist`() = runTest {
        // GIVEN
        val mockSnapshot = mockk<DocumentSnapshot>()

        every { mockDocumentRef.get() } returns mockSuccessfulTask(mockSnapshot)
        every { mockSnapshot.toObject(User::class.java) } returns null

        // WHEN
        val result = repository.getCurrentUser("123")

        // THEN
        assertNull(result)
    }

    @Test
    fun `addUser should return success when firebase succeeds`() = runTest {
        //GIVEN
        every { mockDocumentRef.set(any()) } returns mockSuccessfulTask<Void>(null)

        val user = User(id = "user_id", firstname = "John", lastname = "Doe")

        //WHEN
        val result = repository.addUser(user)

        //THEN
        assertTrue(result.isSuccess)
    }

    @Test
    fun `addUser should return failure when firebase fails`() = runTest {
        //GIVEN
        val firebaseException = Exception("No internet connection")

        every { mockDocumentRef.set(any()) } returns mockFailedTask(firebaseException)

        val user = User(id = "user_id", firstname = "John", lastname = "Doe")

        //WHEN
        val result = repository.addUser(user)

        //THEN
        assertTrue(result.isFailure)
        assertEquals(firebaseException, result.exceptionOrNull())
    }

    @Test
    fun `deleteUser should return success when firebase succeeds`() = runTest {
        //GIVEN
        every { mockDocumentRef.delete() } returns mockSuccessfulTask<Void>(null)

        val userId = "user_id"

        //WHEN
        val result = repository.deleteUser(userId)

        //THEN
        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteUser should return failure when firebase fails`() = runTest {
        //GIVEN
        val firebaseException = Exception("No internet connection")

        every { mockDocumentRef.delete() } returns mockFailedTask(firebaseException)

        val userId = "user_id"

        //WHEN
        val result = repository.deleteUser(userId)

        //THEN
        assertTrue(result.isFailure)
        assertEquals(firebaseException, result.exceptionOrNull())
    }

    private fun <T> mockSuccessfulTask(resultData: T?): Task<T> = mockk {
        every { isComplete } returns true
        every { isSuccessful } returns true
        every { isCanceled } returns false
        every { exception } returns null
        every { result } returns resultData
    }

    private fun <T> mockFailedTask(e: Exception): Task<T> = mockk {
        every { isComplete } returns true
        every { isSuccessful } returns false
        every { isCanceled } returns false
        every { exception } returns e
        every { result } throws e
    }


}