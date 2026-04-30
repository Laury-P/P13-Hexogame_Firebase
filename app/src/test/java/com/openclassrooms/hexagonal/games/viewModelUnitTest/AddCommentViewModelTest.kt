package com.openclassrooms.hexagonal.games.viewModelUnitTest

import app.cash.turbine.test
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.usecases.GetUserUseCase
import com.openclassrooms.hexagonal.games.ui.screen.comment.AddCommentViewModel
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
class AddCommentViewModelTest {

    private val postRepository: PostRepository = mockk()
    private val getUserUseCase: GetUserUseCase = mockk()
    private lateinit var viewModel: AddCommentViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddCommentViewModel(postRepository, getUserUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when content is blank error should be true`() = runTest {
        viewModel.error.test {
            // État initial (vide)
            assertEquals(true, awaitItem())

            // WHEN: On écrit quelque chose
            viewModel.onContentChanged("Super jeu !")
            assertEquals(false, awaitItem())

            // WHEN: On efface tout
            viewModel.onContentChanged("")
            assertEquals(true, awaitItem())
        }
    }

    @Test
    fun `addComment success should transition to Published`() = runTest {
        // GIVEN
        val postId = "post_123"
        val mockUser = mockk<User>() // On crée un utilisateur fictif

        coEvery { getUserUseCase() } returns mockUser
        // On accepte n'importe quel commentaire (match any) car l'ID et le timestamp changent
        coEvery { postRepository.addComment(eq(postId), any()) } returns Result.success(Unit)

        viewModel.isPublishing.test {
            // État initial
            assertEquals(IsPublishing.Idle, awaitItem())

            // WHEN
            viewModel.onContentChanged("Mon commentaire")
            viewModel.addComment(postId)

            // THEN : Vérification de la séquence des états
            assertEquals(IsPublishing.Publishing, awaitItem())
            assertEquals(IsPublishing.Published, awaitItem())
        }
    }

    @Test
    fun `addComment should fail if user is null`() = runTest {
        // GIVEN
        coEvery { getUserUseCase() } returns null

        viewModel.isPublishing.test {
            awaitItem() // Idle

            // WHEN
            viewModel.addComment("123")

            // THEN
            assertEquals(IsPublishing.Publishing, awaitItem())
            assertEquals(IsPublishing.UserError, awaitItem())
        }
    }
}