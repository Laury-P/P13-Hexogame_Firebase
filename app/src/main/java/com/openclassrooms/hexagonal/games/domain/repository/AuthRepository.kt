package com.openclassrooms.hexagonal.games.domain.repository

import android.content.Context
import android.content.Intent
import com.firebase.ui.auth.AuthState
import com.openclassrooms.hexagonal.games.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun userLogState(): Flow<AuthState>
    suspend fun getCurrentUser(): User?
    suspend fun signOut(context: Context)
    suspend fun deleteAccount(context: Context)
}

