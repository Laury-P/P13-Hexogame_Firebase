package com.openclassrooms.hexagonal.games.data.repository

import android.content.Context
import com.firebase.ui.auth.AuthException
import com.firebase.ui.auth.AuthState
import com.firebase.ui.auth.FirebaseAuthUI
import com.google.firebase.Firebase
import com.openclassrooms.hexagonal.games.domain.exception.DomainAuthException
import com.openclassrooms.hexagonal.games.domain.model.LocalAuthState
import com.openclassrooms.hexagonal.games.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class FirebaseUiAuthRepository @Inject constructor(@param:ApplicationContext private val context: Context) : AuthRepository {

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

    override suspend fun signOut(): Result<Unit> = runCatching {
        FirebaseAuthUI.getInstance().signOut(context)
    }

    override fun getUserId(): String?{
        return FirebaseAuthUI.getInstance().auth.currentUser?.uid
    }

    override suspend fun deleteAccount(): Result<Unit> = runCatching {
        try {
            FirebaseAuthUI.getInstance().delete(context)
        } catch (e: Exception) {
            throw when(e) {
                is AuthException.InvalidCredentialsException -> DomainAuthException.NeedsReauth()
                is AuthException.NetworkException -> DomainAuthException.NetworkError()
                else -> DomainAuthException.UnknownError(e.message ?: "Unknown error")
            }
    }}

    override suspend fun checkIfReauthIsNeeded(): Boolean {
        val user = FirebaseAuthUI.getInstance().getCurrentUser() ?: return true
        val lastLogin = user.metadata?.lastSignInTimestamp ?: 0L
        val now = System.currentTimeMillis()

        // Si la session a plus de 5 minute
        return (now - lastLogin) < 300_000
    }

}