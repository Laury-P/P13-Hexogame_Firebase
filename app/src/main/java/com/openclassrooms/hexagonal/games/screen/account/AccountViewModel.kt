package com.openclassrooms.hexagonal.games.screen.account

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.AuthException
import com.openclassrooms.hexagonal.games.domain.usecases.DeleteAccountUseCase
import com.openclassrooms.hexagonal.games.domain.usecases.LogoutUseCase
import com.openclassrooms.hexagonal.games.ui.event.AccountEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {

    private val _events = Channel<AccountEvent>()

    val events = _events.receiveAsFlow()


    fun logout(context: Context) {
        viewModelScope.launch {
            logoutUseCase(context)
        }
    }

    fun deleteAccount(context: Context) {
        viewModelScope.launch {
            try {
                deleteAccountUseCase(context)
               _events.send(AccountEvent.AccountDeleted)
            } catch (e: Exception) {
                when (e) {
                    is AuthException.InvalidCredentialsException -> {
                        _events.send(AccountEvent.NeedReauthentification)
                    }
                    is AuthException.NetworkException -> {
                        _events.send(AccountEvent.NetworkError)
                    }
                    else -> {
                        _events.send(AccountEvent.UnknownError(e.message ?: "Unknown error"))
                    }
                }
            }

        }
    }
}