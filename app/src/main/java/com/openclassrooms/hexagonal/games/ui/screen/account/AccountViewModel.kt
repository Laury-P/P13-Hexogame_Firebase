package com.openclassrooms.hexagonal.games.ui.screen.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.domain.exception.DomainAuthException
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


    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
                .onSuccess {
                    _events.send(AccountEvent.AccountDeleted)
                }
                .onFailure { e ->
                    _events.send(AccountEvent.FailedSignOut)
                    // TODO Send a crashlytics repots?
                }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                deleteAccountUseCase()
               _events.send(AccountEvent.AccountDeleted)
            } catch (e: Exception) {
                when (e) {
                    is DomainAuthException.NeedsReauth -> {
                        _events.send(AccountEvent.NeedReauthentification)
                    }
                    is DomainAuthException.NetworkError -> {
                        _events.send(AccountEvent.NetworkError)
                    }
                    else -> {
                        // !! because default message defined in repository
                        _events.send(AccountEvent.UnknownError(e.message!!))
                    }
                }
            }
        }
    }
}