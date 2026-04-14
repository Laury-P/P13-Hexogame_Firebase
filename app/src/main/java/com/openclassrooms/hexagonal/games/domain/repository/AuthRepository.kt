package com.openclassrooms.hexagonal.games.domain.repository

import com.openclassrooms.hexagonal.games.domain.model.LocalAuthState
import com.openclassrooms.hexagonal.games.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun userLogState(): Flow<LocalAuthState>
    suspend fun getCurrentUser(): User?
    suspend fun signOut()
    suspend fun deleteAccount()
}

