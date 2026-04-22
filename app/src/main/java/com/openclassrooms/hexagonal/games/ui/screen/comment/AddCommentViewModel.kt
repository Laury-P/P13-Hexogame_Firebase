package com.openclassrooms.hexagonal.games.ui.screen.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import com.openclassrooms.hexagonal.games.ui.util.IsPublishing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AddCommentViewModel @Inject constructor(private val postRepository: PostRepository) :
    ViewModel() {

    private val _content = MutableStateFlow("")
    val content = _content.asStateFlow()

    private val _isPublishing = MutableStateFlow<IsPublishing>(IsPublishing.Idle)
    val isPublishing = _isPublishing.asStateFlow()

    val error : StateFlow<Boolean> = _content.map {
        it.isBlank()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = true
    )



    fun onContentChanged(newContent: String) {
        _content.value = newContent
    }

    fun addComment() {

    }

}
