package com.openclassrooms.hexagonal.games.domain.repository

import com.openclassrooms.hexagonal.games.domain.model.LocalAuthState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun userLogState(): Flow<LocalAuthState>
    suspend fun signOut() : Result<Unit>
    suspend fun deleteAccount()
    fun getUserId() : String?

}

