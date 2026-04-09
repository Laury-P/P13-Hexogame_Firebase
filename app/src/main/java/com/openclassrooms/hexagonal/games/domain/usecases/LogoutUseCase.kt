package com.openclassrooms.hexagonal.games.domain.usecases

import android.content.Context
import com.openclassrooms.hexagonal.games.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val authRepository: AuthRepository){
    suspend operator fun invoke(context: Context) {
        authRepository.signOut(context)
    }
}


