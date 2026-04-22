package com.openclassrooms.hexagonal.games.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseUserRepository @Inject constructor(private val firestore: FirebaseFirestore) :
    UserRepository {

    override suspend fun getCurrentUser(userId: String): User? {
        val document = firestore.collection("users").document(userId).get().await()
        return document.toObject(User::class.java)
    }

    override suspend fun addUser(user: User): Result<Unit> = runCatching {
        firestore.collection("users").document(user.id).set(user).await()
    }


}

