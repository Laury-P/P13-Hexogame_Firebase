package com.openclassrooms.hexagonal.games

import com.openclassrooms.hexagonal.games.domain.exception.DomainAuthException
import com.openclassrooms.hexagonal.games.domain.usecases.DeleteAccountUseCase
import com.openclassrooms.hexagonal.games.domain.usecases.LogoutUseCase
import com.openclassrooms.hexagonal.games.ui.event.AccountEvent
import com.openclassrooms.hexagonal.games.ui.screen.account.AccountViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountViewModelTest {

    // On prépare les mocks
    private val logoutUseCase: LogoutUseCase = mockk()
    private val deleteAccountUseCase: DeleteAccountUseCase = mockk()

    private lateinit var viewModel: AccountViewModel

    // Dispatcher pour simuler l'environnement Android
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // On remplace le Main Dispatcher par notre dispatcher de test
        Dispatchers.setMain(testDispatcher)

        viewModel = AccountViewModel(logoutUseCase, deleteAccountUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `logout success should emit AccountDeleted event`() = runTest {
        // GIVEN : On définit que le Use Case renvoie un succès
        coEvery { logoutUseCase() } returns Result.success(Unit)

        // On observe le flux d'événements
        val events = mutableListOf<AccountEvent>()
        val job = launch {
            viewModel.events.toList(events)
        }

        // WHEN : On appelle la fonction de déconnexion
        viewModel.logout()
        advanceUntilIdle() // On attend que la coroutine termine

        // THEN : On vérifie que l'événement reçu est bien AccountDeleted
        assertEquals(AccountEvent.AccountDeleted, events.first())

        job.cancel()
    }

    @Test
    fun `deleteAccount with NeedsReauth error should emit NeedReauthentification event`() = runTest {
        // GIVEN : On simule une erreur de type réauthentification
        val error = DomainAuthException.NeedsReauth()
        coEvery { deleteAccountUseCase() } returns Result.failure(error)

        val events = mutableListOf<AccountEvent>()
        val job = launch {
            viewModel.events.toList(events)
        }

        // WHEN
        viewModel.deleteAccount()
        advanceUntilIdle()

        // THEN
        assertEquals(AccountEvent.NeedReauthentification, events.first())

        job.cancel()
    }
}