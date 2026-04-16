package com.openclassrooms.hexagonal.games.domain.repository

import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val posts: Flow<List<Post>>
    suspend fun addPost(post: Post)
}
