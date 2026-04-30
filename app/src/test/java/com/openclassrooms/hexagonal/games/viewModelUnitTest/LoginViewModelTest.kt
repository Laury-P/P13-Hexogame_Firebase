package com.openclassrooms.hexagonal.games.viewModelUnitTest

import app.cash.turbine.test
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.domain.repository.UserRepository
import com.openclassrooms.hexagonal.games.ui.screen.account.LogViewModel
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
class LogViewModelTest {

    private val userRepository: UserRepository = mockk()
    private lateinit var viewModel: LogViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LogViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `checkUser should set userExists to true when user is found`() = runTest {
        // GIVEN
        val uid = "123"
        val mockUser = mockk<User>() // On crée un utilisateur fictif
        coEvery { userRepository.getCurrentUser(uid) } returns mockUser

        // On utilise Turbine pour tester le Flow
        viewModel.userExists.test {
            // THEN : L'état initial est null
            assertEquals(null, awaitItem())

            // WHEN
            viewModel.checkUser(uid)

            // THEN : Le nouvel état est true
            assertEquals(true, awaitItem())
        }
    }

    @Test
    fun `checkUser should set userExists to false when user is not found`() = runTest {
        // GIVEN
        val uid = "unknown"
        coEvery { userRepository.getCurrentUser(uid) } returns null

        viewModel.userExists.test {
            assertEquals(null, awaitItem()) // État initial

            // WHEN
            viewModel.checkUser(uid)

            // THEN
            assertEquals(false, awaitItem())
        }
    }

    @Test
    fun `createUser success should set userExists to true`() = runTest {
        // GIVEN
        val newUser = User(id = "456", firstname = "Test", lastname = "User")
        coEvery { userRepository.addUser(newUser) } returns Result.success(Unit)

        viewModel.userExists.test {
            assertEquals(null, awaitItem())

            // WHEN
            viewModel.createUser(newUser)

            // THEN
            assertEquals(true, awaitItem())
        }
    }
}