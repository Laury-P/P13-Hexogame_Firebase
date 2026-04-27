package com.openclassrooms.hexagonal.games.data.repository

import com.google.firebase.messaging.FirebaseMessaging
import com.openclassrooms.hexagonal.games.domain.repository.NotificationRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseNotificationRepository @Inject constructor(private val firebaseMessaging: FirebaseMessaging) :
    NotificationRepository {
    override suspend fun subscribeToAllNotification() : Result<Unit> = runCatching {
        firebaseMessaging.subscribeToTopic("all").await()
    }

    override suspend fun unsubscribeFromAllNotification() : Result<Unit> = runCatching {
        firebaseMessaging.unsubscribeFromTopic("all").await()
    }
}