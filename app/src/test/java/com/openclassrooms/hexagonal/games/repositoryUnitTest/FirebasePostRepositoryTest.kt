package com.openclassrooms.hexagonal.games.repositoryUnitTest

import android.net.Uri
import app.cash.turbine.test
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.snapshots
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.openclassrooms.hexagonal.games.data.repository.FirebasePostRepository
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.ui.util.UiState
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FirebasePostRepositoryTest {

    private val firestore: FirebaseFirestore = mockk()
    private val storage: FirebaseStorage = mockk()

    private val mockCollection: CollectionReference = mockk()
    private val mockDocumentRef: DocumentReference = mockk()
    private val mockQuery: Query = mockk()

    private val mockStorageRef: StorageReference = mockk()

    private lateinit var repository: FirebasePostRepository

    @Before
    fun setup() {
        every { firestore.collection("posts") } returns mockCollection
        every { mockCollection.document(any()) } returns mockDocumentRef
        every { mockCollection.orderBy(any<String>(), any()) } returns mockQuery

        every { storage.reference } returns mockStorageRef
        every { mockStorageRef.child(any()) } returns mockStorageRef

        repository = FirebasePostRepository(firestore, storage)
    }

    @Test
    fun `addPost should upload image and set post when photo exists`() = runTest {
        // GIVEN
        val post = Post(id = "p1", title = "Title", photoUrl = "content://path/to/image")

        val mockUploadTask = mockk<UploadTask>{
            every { isComplete } returns true
            every { isSuccessful } returns true
            every { isCanceled } returns false
            every { result } returns mockk()
            every { exception } returns null
        }

        every { mockStorageRef.putFile(any()) } returns mockUploadTask

        every { mockStorageRef.downloadUrl } returns mockSuccessfulTask<Uri>(mockk()) // Simule l'URL de téléchargement

        every { mockDocumentRef.set(any()) } returns mockSuccessfulTask<Void>(null)

        // WHEN
        val result = repository.addPost(post)

        // THEN
        assertTrue(result.isSuccess)
        verify { mockStorageRef.putFile(any()) }
        verify { mockDocumentRef.set(any()) }
    }

    @Test
    fun `addPost should not upload anything when photoUrl is null`() = runTest {
        // GIVEN
        val post = Post(id = "p2", title = "No photo", photoUrl = null)
        every { mockDocumentRef.set(any()) } returns mockSuccessfulTask(null)

        // WHEN
        val result = repository.addPost(post)

        // THEN
        assertTrue(result.isSuccess)
        verify(exactly = 0) { mockStorageRef.putFile(any()) } // Vérifie que putFile n'est JAMAIS appelé
        verify { mockDocumentRef.set(any()) }
    }

    @Test
    fun `addComment should call firestore set`() = runTest {
        // GIVEN
        val comment = Comment(id = "c1", content = "Nice post")
        val mockCommentDoc = mockk<DocumentReference>()

        // On mock le chemin : posts -> document -> collection(comments) -> document(id)
        every { mockDocumentRef.collection("comments") } returns mockCollection
        every { mockCollection.document(comment.id) } returns mockCommentDoc
        every { mockCommentDoc.set(comment) } returns mockSuccessfulTask(null)

        // WHEN
        val result = repository.addComment("p1", comment)

        // THEN
        assertTrue(result.isSuccess)
        verify { mockCommentDoc.set(comment) }
    }

    @Test
    fun `addComment should return failure when firestore fails`() = runTest {
        // GIVEN
        val comment = Comment(id = "c1", content = "Bad luck")
        val mockCommentDoc = mockk<DocumentReference>()
        val exception = RuntimeException("Firestore Error")

        every { mockDocumentRef.collection("comments") } returns mockCollection
        every { mockCollection.document(comment.id) } returns mockCommentDoc
        // On simule une tâche en échec
        every { mockCommentDoc.set(comment) } returns mockFailedTask(exception)

        // WHEN
        val result = repository.addComment("p1", comment)

        // THEN
        assertTrue(result.isFailure)
        assertEquals(exception.message, result.exceptionOrNull()?.message)
    }

    @Test
    fun `getPostById emits Success when firestore returns document`() = runTest {

        mockkStatic(DocumentReference::snapshots)

        val documentSnapshot = mockk<DocumentSnapshot>()

        val fakePost = Post(
            id = "123",
            title = "First",
            description = "First content",
        )

        every { documentSnapshot.toObject(Post::class.java) } returns fakePost
        every {mockDocumentRef.snapshots()} returns flowOf(documentSnapshot)

        val result = repository.getPostById("123").first()

        assertTrue(result is UiState.Success)

        unmockkStatic(DocumentReference::snapshots)
    }

    @Test
    fun `getPostById should emit Error state when firestore fails`() = runTest {
        mockkStatic(DocumentReference::snapshots)
        val errorMessage = "Firestore Error"

        val errorFlow: Flow<DocumentSnapshot> = flow {
            throw RuntimeException(errorMessage)
        }

        every { mockDocumentRef.snapshots() } returns errorFlow

        val result = repository.getPostById("123").first()

        assertTrue(result is UiState.Error)
        assertEquals(errorMessage, (result as UiState.Error).message)

        unmockkStatic(DocumentReference::snapshots)
    }

    @Test
    fun `getCommentsByPostId emits Success with list of comments`() = runTest {
        // GIVEN
        mockkStatic(Query::snapshots)
        val mockCommentSnapshot = mockk<QuerySnapshot>()
        val mockDoc = mockk<QueryDocumentSnapshot>()
        val comment = Comment(id = "c1", content = "Hello")

        every { mockDocumentRef.collection("comments") } returns mockCollection
        every { mockCollection.orderBy("timestamp", Query.Direction.DESCENDING) } returns mockQuery

        every { mockQuery.snapshots() } returns flowOf(mockCommentSnapshot)
        every { mockCommentSnapshot.documents } returns listOf(mockDoc)
        every { mockDoc.toObject(Comment::class.java) } returns comment

        // WHEN & THEN
        repository.getCommentsByPostId("p1").test {
            val result = awaitItem()
            assertTrue(result is UiState.Success)
            assertEquals(1, (result as UiState.Success).data.size)
            assertEquals("Hello", result.data[0].content)
            cancelAndIgnoreRemainingEvents()
        }
        unmockkStatic(Query::snapshots)
    }

    @Test
    fun `getCommentsByPostId emits Error when flow fails`() = runTest {
        // GIVEN
        mockkStatic(Query::snapshots)
        val errorMessage = "Network error"

        every { mockDocumentRef.collection("comments") } returns mockCollection
        every { mockCollection.orderBy("timestamp", Query.Direction.DESCENDING) } returns mockQuery
        every { mockQuery.snapshots() } returns flow { throw RuntimeException(errorMessage) }

        // WHEN & THEN
        repository.getCommentsByPostId("p1").test {
            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals(errorMessage, (result as UiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
        unmockkStatic(Query::snapshots)
    }

    @Test
    fun `deleteAllPostsFromUser should delete comments, photos and the post itself`() = runTest {
        // 1. GIVEN
        val userId = "u1"
        val mockBatch = mockk<WriteBatch>(relaxed = true)
        val mockPostDoc = mockk<QueryDocumentSnapshot>()
        val mockCommentDoc = mockk<QueryDocumentSnapshot>()
        val mockPostReference = mockk<DocumentReference>() // La référence du POST
        val mockCommentCollection = mockk<CollectionReference>() // La collection COMMENTS


        every { firestore.batch() } returns mockBatch
        every {
            firestore.collection("posts")
                .whereEqualTo("author.id", userId)
                .get()
        } returns mockSuccessfulTask(mockk {
            every { documents } returns listOf(mockPostDoc)
        })

        every { mockPostDoc.reference } returns mockPostReference
        every { mockPostReference.collection("comments") } returns mockCommentCollection
        every { mockCommentCollection.get() } returns mockSuccessfulTask(mockk {
            every { documents } returns listOf(mockCommentDoc)
        })

        val mockCommentReference = mockk<DocumentReference>()
        every { mockCommentDoc.reference } returns mockCommentReference

        val photoUrl = "https://firebasestorage.com/image.jpg"
        every { mockPostDoc.getString("photoUrl") } returns photoUrl
        val mockPhotoRef = mockk<StorageReference>()
        every { storage.getReferenceFromUrl(photoUrl) } returns mockPhotoRef
        every { mockPhotoRef.delete() } returns mockSuccessfulTask(null)

        every { mockBatch.commit() } returns mockSuccessfulTask(null)

        // WHEN
        val result = repository.deleteAllPostsFromUser(userId)

        // THEN

        assertTrue(result.isSuccess)
        verify { mockBatch.delete(mockCommentReference) } // Vérifie suppression commentaire
        verify { mockPhotoRef.delete() }                  // Vérifie suppression photo
        verify { mockBatch.delete(mockPostReference) }    // Vérifie suppression post
        verify { mockBatch.commit() }
    }

    @Test
    fun `deleteAllCommentsFromUser should delete all user comments using collectionGroup`() = runTest {
        // GIVEN
        val userId = "u1"
        val mockBatch = mockk<WriteBatch>(relaxed = true)
        val mockCommentDoc = mockk<QueryDocumentSnapshot>()
        val mockCommentRef = mockk<DocumentReference>()

        every { firestore.batch() } returns mockBatch
        every {
            firestore.collectionGroup("comments")
                .whereEqualTo("author.id", userId)
                .get()
        } returns mockSuccessfulTask(mockk {
            every { documents } returns listOf(mockCommentDoc)
        })

        every { mockCommentDoc.reference } returns mockCommentRef
        every { mockBatch.commit() } returns mockSuccessfulTask(null)

        // WHEN
        val result = repository.deleteAllCommentsFromUser(userId)

        // THEN
        assertTrue(result.isSuccess)
        verify { mockBatch.delete(mockCommentRef) }
        verify { mockBatch.commit() }
    }

    private fun <T> mockSuccessfulTask(resultData: T?): Task<T> = mockk {
        every { isComplete } returns true
        every { isSuccessful } returns true
        every { isCanceled } returns false
        every { exception } returns null
        every { result } returns resultData
    }

    private fun <T> mockFailedTask(e: Exception): Task<T> = mockk{
        every { isComplete } returns true
        every { isSuccessful } returns false
        every { isCanceled } returns false
        every { exception } returns e
        every { result } throws e
    }
}