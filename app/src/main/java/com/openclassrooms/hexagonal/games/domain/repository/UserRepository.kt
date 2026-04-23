package com.openclassrooms.hexagonal.games.domain.repository

import com.openclassrooms.hexagonal.games.domain.model.User

interface UserRepository {
   suspend fun getCurrentUser(userId: String): User?
   suspend fun addUser(user: User): Result<Unit>
   suspend fun deleteUser(userId: String): Result<Unit>
}


