package com.openclassrooms.hexagonal.games.domain.model

sealed class LocalAuthState {
    object Loading : LocalAuthState()
    object LoggedIn : LocalAuthState()
    object LoggedOut : LocalAuthState()
}
