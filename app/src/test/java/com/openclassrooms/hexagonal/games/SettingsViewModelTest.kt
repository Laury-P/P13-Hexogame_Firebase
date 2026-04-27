package com.openclassrooms.hexagonal.games

import app.cash.turbine.test
import com.openclassrooms.hexagonal.games.domain.repository.NotificationRepository
import com.openclassrooms.hexagonal.games.ui.screen.settings.SettingsEvents
import com.openclassrooms.hexagonal.games.ui.screen.settings.SettingsViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val notificationRepository: NotificationRepository = mockk()
    private lateinit var viewModel: SettingsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SettingsViewModel(notificationRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `enableNotifications success should emit EnabledNotifications event`() = runTest {
        // GIVEN : On simule un succès du repository
        coEvery { notificationRepository.subscribeToAllNotification() } returns Result.success(Unit)

        viewModel.settingsEvents.test {
            // WHEN
            viewModel.enableNotifications()

            // THEN : On vérifie l'événement reçu
            assertEquals(SettingsEvents.EnabledNotifications, awaitItem())
        }
    }

    @Test
    fun `disableNotifications success should emit DisabledNotifications event`() = runTest {
        // GIVEN
        coEvery { notificationRepository.unsubscribeFromAllNotification() } returns Result.success(Unit)

        viewModel.settingsEvents.test {
            // WHEN
            viewModel.disableNotifications()

            // THEN
            assertEquals(SettingsEvents.DisabledNotifications, awaitItem())
        }
    }

    @Test
    fun `enableNotifications failure should emit Error event`() = runTest {
        // GIVEN : On simule un échec (par exemple une exception réseau)
        coEvery { notificationRepository.subscribeToAllNotification() } returns Result.failure(Exception("Network error"))

        viewModel.settingsEvents.test {
            // WHEN
            viewModel.enableNotifications()

            // THEN
            assertEquals(SettingsEvents.Error, awaitItem())
        }
    }
}