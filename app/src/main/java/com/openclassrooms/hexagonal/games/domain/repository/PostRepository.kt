package com.openclassrooms.hexagonal.games.domain.repository

import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.ui.util.UiState
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val posts: Flow<List<Post>>
    suspend fun addPost(post: Post): Result<Unit>
    suspend fun addComment(postId: String, comment: Comment): Result<Unit>
    fun getPostById(postId: String): Flow<UiState<Post?>>
    fun getCommentsByPostId(postId: String): Flow<UiState<List<Comment>>>
    suspend fun deleteAllPostsFromUser(userId: String): Result<Unit>
    suspend fun deleteAllCommentsFromUser(userId: String): Result<Unit>

}
