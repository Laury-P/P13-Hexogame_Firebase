package com.openclassrooms.hexagonal.games.data.repository

import android.content.Context
import android.content.Intent
import com.firebase.ui.auth.AuthState
import com.firebase.ui.auth.FirebaseAuthUI
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow


class FirebaseUiAuthRepository : AuthRepository {

    override fun userLogState(): Flow<AuthState> {
        return FirebaseAuthUI.getInstance().authStateFlow()
    }

    override suspend fun getCurrentUser(): User? {
        TODO("Not yet implemented")
    }

    override suspend fun signOut(context: Context) {
        return FirebaseAuthUI.getInstance().signOut(context)
    }

    override suspend fun deleteAccount() {
        TODO("Not yet implemented")
    }

}