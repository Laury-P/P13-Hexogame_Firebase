package com.openclassrooms.hexagonal.games.ui.screen.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.usecases.GetUserUseCase
import com.openclassrooms.hexagonal.games.ui.util.IsPublishing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCommentViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val getUserUseCase: GetUserUseCase
) :
    ViewModel() {

    private val _content = MutableStateFlow("")
    val content = _content.asStateFlow()

    private val _isPublishing = MutableStateFlow<IsPublishing>(IsPublishing.Idle)
    val isPublishing = _isPublishing.asStateFlow()

    val error: StateFlow<Boolean> = _content.map {
        it.isBlank()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = true
    )


    fun onContentChanged(newContent: String) {
        _content.value = newContent
    }

    fun addComment(postId: String) {
        viewModelScope.launch {
            _isPublishing.value = IsPublishing.Publishing
            val user = getUserUseCase()

            if (user != null) {
                val newComment = Comment(
                    id = "UUID.randomUUID().toString()",
                    content = _content.value,
                    author = user,
                    timestamp = System.currentTimeMillis(),
                    postId = postId
                )
                val result = postRepository.addComment(
                    postId,
                    comment = newComment,
                )
                if (result.isSuccess) _isPublishing.value = IsPublishing.Published

            } else {
                // TODO : Handle the case where user is null
                _isPublishing.value = IsPublishing.Idle
            }

        }

    }

}
