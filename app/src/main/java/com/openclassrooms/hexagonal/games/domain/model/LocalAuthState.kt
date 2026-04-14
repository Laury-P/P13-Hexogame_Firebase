package com.openclassrooms.hexagonal.games.domain.model

sealed class LocalAuthState {
    object Loading : LocalAuthState()
    class LoggedIn (val userId: String) : LocalAuthState()
    object LoggedOut : LocalAuthState()
}
