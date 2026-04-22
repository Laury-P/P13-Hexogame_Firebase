package com.openclassrooms.hexagonal.games.domain.usecases

import com.openclassrooms.hexagonal.games.domain.repository.AuthRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke() : Result<Unit> {
        //TODO Supprimer les données utilisateur avant de supprimé le compte
        return authRepository.deleteAccount()
    }
}