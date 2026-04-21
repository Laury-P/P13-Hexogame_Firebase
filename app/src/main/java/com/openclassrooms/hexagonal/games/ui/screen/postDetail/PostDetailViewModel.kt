package com.openclassrooms.hexagonal.games.ui.screen.postDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.LocalAuthState
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.usecases.GetUserLogStateUseCase
import com.openclassrooms.hexagonal.games.ui.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    getUserLogStateUseCase: GetUserLogStateUseCase
) : ViewModel() {

    val authState : StateFlow<LocalAuthState> = getUserLogStateUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), LocalAuthState.LoggedOut)

    private val _post = MutableStateFlow<UiState<Post?>>(UiState.Idle)
    val post: StateFlow<UiState<Post?>> = _post

    private val _comments = MutableStateFlow<UiState<List<Comment>>>(UiState.Idle)
    val comments: StateFlow<UiState<List<Comment>>> = _comments

    fun loadPost(postId: String) {
        if(_post.value is UiState.Idle || _post.value is UiState.Error) { // evite de relancer l'appel si chargement en cours ou en success
            viewModelScope.launch {
                _post.value = UiState.Loading
                postRepository.getPostById(postId).collect { post ->
                    _post.value = post
                }
            }
        }
        if(_comments.value is UiState.Idle || _comments.value is UiState.Error){
            viewModelScope.launch {
                _comments.value = UiState.Loading
                postRepository.getCommentsByPostId(postId).collect {
                    _comments.value = it
                }
            }
        }
    }

}