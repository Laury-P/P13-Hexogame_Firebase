package com.openclassrooms.hexagonal.games.screen.account

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.domain.usecases.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(private val logoutUseCase: LogoutUseCase) : ViewModel() {

    fun logout(context: Context){
        viewModelScope.launch{
            logoutUseCase(context)
        }
    }
}