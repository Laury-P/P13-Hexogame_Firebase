package com.openclassrooms.hexagonal.games.screen.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    private val _userExists = MutableStateFlow<Boolean?>(null)
    val userExists = _userExists.asStateFlow()

    fun checkUser(uid: String) {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser(uid)
            _userExists.value = user != null
        }
    }

    fun createUser(user: User) {
        viewModelScope.launch {
            try {
                userRepository.addUser(user)
                _userExists.value = true
            } catch (e: Exception) {
                //TODO Gérer les erreurs coté UI
                Log.e("LogViewModel", "Error creating user", e)
            }
        }
    }

}