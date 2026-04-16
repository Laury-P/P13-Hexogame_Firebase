package com.openclassrooms.hexagonal.games.data.repository

import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FirebasePostRepository @Inject constructor(private val firestore: FirebaseFirestore, private val storage: FirebaseStorage) : PostRepository{
    override val posts: Flow<List<Post>>
        get() = TODO("Not yet implemented")

    override suspend fun addPost(post: Post) {
        try {
            Log.d("DEBUG_POST", "debut de addpost repository")
            var finalImageUrl: String? = null

            if (post.photoUrl != null) {
                val fileName = UUID.randomUUID().toString()
                val imageRef = storage.reference.child("images/$fileName")

                imageRef.putFile(post.photoUrl.toUri()).await()

                finalImageUrl = imageRef.downloadUrl.await().toString()
            }
            val newPost = post.copy(photoUrl = finalImageUrl)

            firestore.collection("posts").add(newPost).await()
        } catch (e: Exception) {
            Log.e("FirebasePostRepository", e.message ?: "Unknown error")
        }


    }
}