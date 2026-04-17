package com.openclassrooms.hexagonal.games.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.hexagonal.games.data.repository.FirebasePostRepository
import com.openclassrooms.hexagonal.games.data.repository.FirebaseUiAuthRepository
import com.openclassrooms.hexagonal.games.data.repository.FirebaseUserRepository
import com.openclassrooms.hexagonal.games.data.service.PostApi
import com.openclassrooms.hexagonal.games.data.service.PostFakeApi
import com.openclassrooms.hexagonal.games.domain.repository.AuthRepository
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
  /**
   * Provides a Singleton instance of PostApi using a PostFakeApi implementation for testing purposes.
   * This means that whenever a dependency on PostApi is requested, the same instance of PostFakeApi will be used
   * throughout the application, ensuring consistent data for testing scenarios.
   *
   * @return A Singleton instance of PostFakeApi.
   */
  @Provides
  @Singleton
  fun providePostApi(): PostApi {
    return PostFakeApi()
  }

  @Provides
  @Singleton
  fun provideAuthRepository(
    @ApplicationContext context: Context
  ): AuthRepository {
    return FirebaseUiAuthRepository(context)
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
}
