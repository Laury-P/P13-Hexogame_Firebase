package com.openclassrooms.hexagonal.games.domain.usecases

import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.domain.repository.AuthRepository
import com.openclassrooms.hexagonal.games.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val authRepository: AuthRepository, private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String): User? {
        val id = authRepository.getUserId()
        return userRepository.getCurrentUser(userId)
    }
}