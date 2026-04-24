package com.openclassrooms.hexagonal.games

import app.cash.turbine.test
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.usecases.GetUserUseCase
import com.openclassrooms.hexagonal.games.ui.screen.ad.AddViewModel
import com.openclassrooms.hexagonal.games.ui.screen.ad.FormError
import com.openclassrooms.hexagonal.games.ui.screen.ad.FormEvent
import com.openclassrooms.hexagonal.games.ui.util.IsPublishing
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
class AddViewModelTest {

    private val postRepository: PostRepository = mockk()
    private val getUserUseCase: GetUserUseCase = mockk()
    private lateinit var viewModel: AddViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddViewModel(postRepository, getUserUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onAction TitleChanged should update post title`() = runTest {
        // WHEN
        viewModel.onAction(FormEvent.TitleChanged("Nouveau Titre"))

        // THEN
        assertEquals("Nouveau Titre", viewModel.post.value.title)
    }

    @Test
    fun `validation should return TitleError when title is empty`() = runTest {
        viewModel.error.test {
            // Valeur initial du stateFlow error
            assertEquals(null, awaitItem())

            // Initialement le titre est vide
            assertEquals(FormError.TitleError, awaitItem())

            // WHEN: On ajoute un titre mais pas de description/photo
            viewModel.onAction(FormEvent.TitleChanged("Titre"))
            assertEquals(FormError.DescriptionOrPhotoError, awaitItem())

            // WHEN: On ajoute une description
            viewModel.onAction(FormEvent.DescriptionChanged("Une description"))
            assertEquals(null, awaitItem()) // Plus d'erreur
        }
    }

    @Test
    fun `addPost success should emit correct publishing states`() = runTest {
        // GIVEN
        val mockUser = mockk<User>()
        coEvery { getUserUseCase() } returns mockUser
        coEvery { postRepository.addPost(any()) } returns Result.success(Unit)

        viewModel.isPublishing.test {
            assertEquals(IsPublishing.Idle, awaitItem())

            // WHEN
            viewModel.onAction(FormEvent.TitleChanged("Titre"))
            viewModel.onAction(FormEvent.DescriptionChanged("Description"))
            viewModel.addPost()

            // THEN
            assertEquals(IsPublishing.Publishing, awaitItem())
            assertEquals(IsPublishing.Published, awaitItem())
        }
    }

    @Test
    fun `addPost should fail when user is not logged in`() = runTest {
        // GIVEN
        coEvery { getUserUseCase() } returns null

        viewModel.isPublishing.test {
            awaitItem() // Idle

            // WHEN
            viewModel.addPost()

            // THEN
            assertEquals(IsPublishing.Publishing, awaitItem())
            assertEquals(IsPublishing.UserError, awaitItem())
        }
    }
}