package com.openclassrooms.hexagonal.games.domain.usecases

import com.openclassrooms.hexagonal.games.domain.exception.DomainAuthException
import com.openclassrooms.hexagonal.games.domain.repository.AuthRepository
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.repository.UserRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        val userId = authRepository.getUserId()
        if (userId == null || authRepository.checkIfReauthIsNeeded()) {
            return Result.failure(DomainAuthException.NeedsReauth())
        }
        postRepository.deleteAllPostsFromUser(userId)
            .onFailure {
                return Result.failure(Exception("Failed to delete posts"))
            }

        postRepository.deleteAllCommentsFromUser(userId)
            .onFailure {
                return Result.failure(Exception("Failed to delete comments"))
            }

        userRepository.deleteUser(userId)
            .onFailure {
                return Result.failure(Exception("Failed to delete user"))
            }

        return authRepository.deleteAccount()
    }


}