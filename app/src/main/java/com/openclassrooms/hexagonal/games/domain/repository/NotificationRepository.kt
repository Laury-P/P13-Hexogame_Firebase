package com.openclassrooms.hexagonal.games.domain.repository

interface NotificationRepository {
    suspend fun subscribeToAllNotification() : Result<Unit>
    suspend fun unsubscribeFromAllNotification() : Result<Unit>
}


