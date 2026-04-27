package com.openclassrooms.hexagonal.games.domain.repository

interface NotificationRepository {
    suspend fun subscribeToAllNotification()
    suspend fun unsubscribeFromAllNotification()
}


