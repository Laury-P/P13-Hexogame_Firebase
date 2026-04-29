package com.openclassrooms.hexagonal.games.data.repository

import androidx.core.net.toUri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.snapshots
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import com.openclassrooms.hexagonal.games.ui.util.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FirebasePostRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : PostRepository {

    override val posts: Flow<List<Post>> = firestore.collection("posts")
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .snapshots()
        .map { querySnapshot ->
            querySnapshot.documents.mapNotNull { document ->
                document.toObject(Post::class.java)
            }
        }

    override suspend fun addPost(post: Post): Result<Unit> = runCatching {
        var finalImageUrl: String? = null

        if (post.photoUrl != null) {
            val fileName = UUID.randomUUID().toString()
            val imageRef = storage.reference.child("images/$fileName")

            imageRef.putFile(post.photoUrl.toUri()).await()

            finalImageUrl = imageRef.downloadUrl.await().toString()
        }
        val newPost = post.copy(photoUrl = finalImageUrl)

        firestore.collection("posts").document(post.id).set(newPost).await()
    }

    override suspend fun addComment(postId: String, comment: Comment): Result<Unit> = runCatching {
        firestore.collection("posts")
            .document(postId)
            .collection("comments")
            .document(comment.id)
            .set(comment)
            .await()
    }

    override fun getPostById(postId: String): Flow<UiState<Post?>> =
        firestore.collection("posts")
            .document(postId)
            .snapshots()
            .map<DocumentSnapshot, UiState<Post?>> { document ->
                UiState.Success(document.toObject(Post::class.java))
            }
            .catch { exception ->
                emit(UiState.Error(exception.message ?: "Unknown error"))
            }

    override fun getCommentsByPostId(postId: String): Flow<UiState<List<Comment>>> =
        firestore.collection("posts")
            .document(postId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .snapshots()
            .map<QuerySnapshot, UiState<List<Comment>>> { queryDocumentSnapshots ->
                val comments = queryDocumentSnapshots.documents.mapNotNull { document ->
                    document.toObject(Comment::class.java)
                }
                UiState.Success(comments)
            }.catch { exception ->
                emit(UiState.Error(exception.message ?: "Unknown error"))
            }

    override suspend fun deleteAllPostsFromUser(userId: String): Result<Unit> = runCatching {
        val batch = firestore.batch()

        val posts = firestore.collection("posts")
            .whereEqualTo("author.id", userId)
            .get()
            .await()

        posts.documents.forEach { document ->
            // Préparation de la suppression de tous les commentaires du posts
            val comments = document.reference.collection("comments").get().await()
            comments.documents.forEach { commentDocument ->
                batch.delete(commentDocument.reference)
            }

            // Suppression de la photo
            val photoUrl = document.getString("photoUrl")
            if (!photoUrl.isNullOrEmpty()) {
                storage.getReferenceFromUrl(photoUrl).delete().await()
            }

            // Préparation de la suppression du post
            batch.delete(document.reference)
        }
        batch.commit().await()
    }

    override suspend fun deleteAllCommentsFromUser(userId: String): Result<Unit> = runCatching {
        val batch = firestore.batch()

        val comments = firestore.collectionGroup("comments")
            .whereEqualTo("author.id", userId)
            .get()
            .await()

        comments.documents.forEach { document ->
            batch.delete(document.reference)
        }
        batch.commit().await()
    }


}