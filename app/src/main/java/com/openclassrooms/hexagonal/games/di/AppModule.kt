package com.openclassrooms.hexagonal.games.di

import android.content.Context
import com.firebase.ui.auth.FirebaseAuthUI
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.hexagonal.games.data.repository.FirebaseNotificationRepository
import com.openclassrooms.hexagonal.games.data.repository.FirebasePostRepository
import com.openclassrooms.hexagonal.games.data.repository.FirebaseUiAuthRepository
import com.openclassrooms.hexagonal.games.data.repository.FirebaseUserRepository
import com.openclassrooms.hexagonal.games.domain.repository.AuthRepository
import com.openclassrooms.hexagonal.games.domain.repository.NotificationRepository
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * This class acts as a Dagger Hilt module, responsible for providing dependencies to other parts of the application.
 * It's installed in the SingletonComponent, ensuring that dependencies provided by this module are created only once
 * and remain available throughout the application's lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

  @Provides
  @Singleton
  fun provideAuthUi() : FirebaseAuthUI {
    return FirebaseAuthUI.getInstance()
  }

  @Provides
  @Singleton
  fun provideAuthRepository(
    @ApplicationContext context: Context,
    authUi: FirebaseAuthUI
  ): AuthRepository {
    return FirebaseUiAuthRepository(context, authUi)
  }

  @Provides
  @Singleton
  fun provideFirestore() : FirebaseFirestore {
    return FirebaseFirestore.getInstance()
  }

  @Provides
  @Singleton
  fun provideStorage() : FirebaseStorage {
    return FirebaseStorage.getInstance()
  }



  @Provides
  @Singleton
  fun provideUserRepository(firestore: FirebaseFirestore) : UserRepository {
    return FirebaseUserRepository(firestore)
  }

  @Provides
  @Singleton
  fun providePostRepository(firestore: FirebaseFirestore, storage: FirebaseStorage) : PostRepository {
    return FirebasePostRepository(firestore, storage)
  }

  @Provides
  @Singleton
  fun provideFirebaseMessaging() : FirebaseMessaging {
    return FirebaseMessaging.getInstance()
  }

  @Provides
  @Singleton
  fun provideNotificationRepository(firebaseMessaging: FirebaseMessaging) : NotificationRepository {
    return FirebaseNotificationRepository(firebaseMessaging)
  }
}
