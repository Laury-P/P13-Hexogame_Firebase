package com.openclassrooms.hexagonal.games.domain.model

sealed class LocalAuthState {
    object Loading : LocalAuthState()
    data class LoggedIn (val uid: String): LocalAuthState()
    object LoggedOut : LocalAuthState()
}
