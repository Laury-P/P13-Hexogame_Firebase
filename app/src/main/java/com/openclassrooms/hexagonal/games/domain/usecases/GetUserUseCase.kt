package com.openclassrooms.hexagonal.games.domain.usecases

import com.openclassrooms.hexagonal.games.domain.exception.DomainAuthException
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.domain.repository.AuthRepository
import com.openclassrooms.hexagonal.games.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): User? {
        val id = authRepository.getUserId() ?: throw DomainAuthException.NeedsReauth()
        return userRepository.getCurrentUser(id)
    }
}