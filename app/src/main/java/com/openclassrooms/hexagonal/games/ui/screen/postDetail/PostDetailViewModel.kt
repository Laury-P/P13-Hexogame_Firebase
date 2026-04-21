package com.openclassrooms.hexagonal.games.ui.screen.postDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    fun loadPost(postId: String) {
        viewModelScope.launch {
            postRepository.getPostById(postId).collect { post ->
                _post.value = post
            }
        }
        viewModelScope.launch {
            postRepository.getCommentsByPostId(postId).collect {
                _comments.value = it
            }
        }
    }

}