package com.openclassrooms.hexagonal.games.domain.usecases

import com.firebase.ui.auth.AuthState
import com.openclassrooms.hexagonal.games.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserLogStateUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(): Flow<AuthState> {
        return authRepository.userLogState()
    }

}