package com.openclassrooms.hexagonal.games.viewModelUnitTest

import app.cash.turbine.test
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.LocalAuthState
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.usecases.GetUserLogStateUseCase
import com.openclassrooms.hexagonal.games.ui.screen.postDetail.PostDetailViewModel
import com.openclassrooms.hexagonal.games.ui.util.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostDetailViewModelTest {

    private val postRepository: PostRepository = mockk()
    private val getUserLogStateUseCase: GetUserLogStateUseCase = mockk()

    private lateinit var viewModel: PostDetailViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getUserLogStateUseCase() } returns flowOf(LocalAuthState.LoggedOut)

        viewModel = PostDetailViewModel(postRepository, getUserLogStateUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPost should emit Loading then Success state`() = runTest {
        // GIVEN
        val postId = "post_1"
        val mockPost = mockk<Post>()
        val postFlow = flowOf(UiState.Success(mockPost))
        val commentsFlow = flowOf(UiState.Success(emptyList<Comment>()))

        coEvery { postRepository.getPostById(postId) } returns postFlow
        coEvery { postRepository.getCommentsByPostId(postId) } returns commentsFlow

        viewModel.post.test {
            // État initial
            assertEquals(UiState.Idle, awaitItem())

            // WHEN
            viewModel.loadPost(postId)

            // THEN
            assertEquals(UiState.Loading, awaitItem())
            val result = awaitItem()
            assertTrue(result is UiState.Success)
            assertEquals(mockPost, (result as UiState.Success).data)
        }
    }

    @Test
    fun `loadPost should not trigger new collect if state is already Success`() = runTest {
        // GIVEN
        val postId = "post_1"
        val postFlow = flowOf(UiState.Success(mockk<Post>()))
        coEvery { postRepository.getPostById(postId) } returns postFlow
        coEvery { postRepository.getCommentsByPostId(postId) } returns flowOf(UiState.Success(emptyList()))

        // Premier chargement
        viewModel.loadPost(postId)
        advanceUntilIdle()

        // WHEN: On tente de recharger alors que c'est déjà chargé
        viewModel.loadPost(postId)
        advanceUntilIdle()

        // THEN: getPostById ne doit avoir été appelé qu'une seule fois
        coVerify(exactly = 1) { postRepository.getPostById(postId) }
    }
}