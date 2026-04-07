package com.openclassrooms.hexagonal.games.domain.repository

import android.content.Intent
import com.firebase.ui.auth.AuthState
import com.openclassrooms.hexagonal.games.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun userLogState(): Flow<AuthState>
    suspend fun getCurrentUser(): User?
    suspend fun getAuthIntent(): Intent
    suspend fun signOut()
    suspend fun deleteAccount()
}

