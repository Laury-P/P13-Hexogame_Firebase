package com.openclassrooms.hexagonal.games.ui.event

sealed class AccountEvent {
    object NeedReauthentification : AccountEvent()
    object NetworkError : AccountEvent()
    object UnknownError : AccountEvent()
    object AccountDeleted : AccountEvent()
    object FailedSignOut : AccountEvent()
    object SuccessSignOut : AccountEvent()
}

