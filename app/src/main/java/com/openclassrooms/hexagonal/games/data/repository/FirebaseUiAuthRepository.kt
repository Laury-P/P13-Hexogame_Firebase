package com.openclassrooms.hexagonal.games.data.repository

import android.content.Context
import com.firebase.ui.auth.AuthState
import com.firebase.ui.auth.FirebaseAuthUI
import com.openclassrooms.hexagonal.games.domain.model.LocalAuthState
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow


class FirebaseUiAuthRepository : AuthRepository {

    override fun userLogState(): Flow<LocalAuthState> {
        return FirebaseAuthUI.getInstance()
            .authStateFlow()
            .map { firebaseAuthState ->
                when (firebaseAuthState) {
                    is AuthState.Success -> {
                        LocalAuthState.LoggedIn(firebaseAuthState.user.uid)
                    }
                    is AuthState.RequiresEmailVerification -> {
                        LocalAuthState.LoggedIn(firebaseAuthState.user.uid)
                    }
                    is AuthState.Loading -> LocalAuthState.Loading
                    else -> LocalAuthState.LoggedOut
                }
            }
    }

    override suspend fun getCurrentUser(): User? {
        TODO("Not yet implemented")
    }

    override suspend fun signOut(context: Context) {
        return FirebaseAuthUI.getInstance().signOut(context)
    }

    override suspend fun deleteAccount(context: Context) {
        return FirebaseAuthUI.getInstance().delete(context)
    }

}