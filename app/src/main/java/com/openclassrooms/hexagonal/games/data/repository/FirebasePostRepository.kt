package com.openclassrooms.hexagonal.games.data.repository

import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
            .map { document ->
                UiState.Success(document.toObject(Post::class.java))
            }
            .catch { exception ->
                UiState.Error(exception.message ?: "Unknown error")
            }

    override fun getCommentsByPostId(postId: String): Flow<UiState<List<Comment>>> =
        firestore.collection("posts")
            .document(postId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .snapshots()
            .map { queryDocumentSnapshots ->
                val comments = queryDocumentSnapshots.documents.mapNotNull { document ->
                    document.toObject(Comment::class.java)
                }
                UiState.Success(comments)
            }.catch { exception ->
                UiState.Error(exception.message ?: "Unknown error")
            }
}