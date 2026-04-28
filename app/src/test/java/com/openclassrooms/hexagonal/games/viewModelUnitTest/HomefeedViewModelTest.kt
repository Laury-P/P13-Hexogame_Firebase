package com.openclassrooms.hexagonal.games.viewModelUnitTest

import app.cash.turbine.test
import com.openclassrooms.hexagonal.games.domain.model.LocalAuthState
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.usecases.GetUserLogStateUseCase
import com.openclassrooms.hexagonal.games.ui.screen.homefeed.HomefeedViewModel
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomefeedViewModelTest {

    private val postRepository: PostRepository = mockk()
    private val getUserLogStateUseCase: GetUserLogStateUseCase = mockk()

    // On prépare un Flow pour simuler les données du repository
    private val postsFlow = MutableStateFlow<List<Post>>(emptyList())
    // On prépare un Flow pour simuler l'état d'auth
    private val authFlow = MutableStateFlow<LocalAuthState>(LocalAuthState.LoggedOut)

    private lateinit var viewModel: HomefeedViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { postRepository.posts } returns postsFlow
        every { getUserLogStateUseCase() } returns authFlow

        viewModel = HomefeedViewModel(postRepository, getUserLogStateUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when repository emits posts viewModel should update posts state`() = runTest {
        val mockPosts = listOf(
            Post(id = "1", title = "Post 1", description = "Desc 1", author = null, timestamp = 0L)
        )

        viewModel.posts.test {
            // État initial (emptyList)
            assertEquals(emptyList<Post>(), awaitItem())

            // WHEN: Le repository émet de nouvelles données
            postsFlow.value = mockPosts

            // THEN
            assertEquals(mockPosts, awaitItem())
        }
    }

    @Test
    fun `authState should reflect getUserLogStateUseCase emissions`() = runTest {
        viewModel.authState.test {
            // Valeur initiale définie dans stateIn
            assertEquals(LocalAuthState.LoggedOut, awaitItem())

            // WHEN: L'utilisateur se connecte
            authFlow.value = LocalAuthState.LoggedIn(uid = "123")

            // THEN
            assertEquals(LocalAuthState.LoggedIn(uid = "123"), awaitItem())
        }
    }
}