package com.openclassrooms.hexagonal.games.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing user settings, specifically notification preferences.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(private val notificationRepository: NotificationRepository) : ViewModel() {

  val settingsEvents = MutableSharedFlow<SettingsEvents>()

  /**
   * Enables notifications for the application.
   */
  fun enableNotifications() {
    viewModelScope.launch {
      notificationRepository.subscribeToAllNotification()
        .onFailure {
          settingsEvents.emit(SettingsEvents.Error)
        }
        .onSuccess {
          settingsEvents.emit(SettingsEvents.EnabledNotifications)
        }
    }
  }
  
  /**
   * Disables notifications for the application.
   */
  fun disableNotifications() {
    viewModelScope.launch {
      notificationRepository.unsubscribeFromAllNotification()
        .onFailure {
          settingsEvents.emit(SettingsEvents.Error)
        }
        .onSuccess {
          settingsEvents.emit(SettingsEvents.DisabledNotifications)
        }
    }
  }
  
}

sealed class SettingsEvents{
  object EnabledNotifications : SettingsEvents()
  object DisabledNotifications : SettingsEvents()
  object Error : SettingsEvents()
}



