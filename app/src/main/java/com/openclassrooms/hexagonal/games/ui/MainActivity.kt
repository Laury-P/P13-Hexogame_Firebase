package com.openclassrooms.hexagonal.games.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.hexagonal.games.ui.screen.Screen
import com.openclassrooms.hexagonal.games.ui.screen.account.AccountScreen
import com.openclassrooms.hexagonal.games.ui.screen.account.LogScreen
import com.openclassrooms.hexagonal.games.ui.screen.ad.AddScreen
import com.openclassrooms.hexagonal.games.ui.screen.comment.AddCommentScreen
import com.openclassrooms.hexagonal.games.ui.screen.homefeed.HomefeedScreen
import com.openclassrooms.hexagonal.games.ui.screen.postDetail.PostDetailScreen
import com.openclassrooms.hexagonal.games.ui.screen.settings.SettingsScreen
import com.openclassrooms.hexagonal.games.ui.theme.HexagonalGamesTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the application. This activity serves as the entry point and container for the navigation
 * fragment. It handles setting up the toolbar, navigation controller, and action bar behavior.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    checkNotificationPermission()

    setContent {
      val navController = rememberNavController()

      HexagonalGamesTheme {
        HexagonalGamesNavHost(navHostController = navController)
      }
    }
  }

  private fun checkNotificationPermission() {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
      val hasPermissions = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED

      if (!hasPermissions) {
        ActivityCompat.requestPermissions(
          this,
          arrayOf(Manifest.permission.POST_NOTIFICATIONS),
          101
        )
      }
    }
  }

}

@Composable
fun HexagonalGamesNavHost(navHostController: NavHostController) {
  NavHost(
    navController = navHostController,
    startDestination = Screen.Homefeed.route
  ) {
    composable(route = Screen.Homefeed.route) {
      HomefeedScreen(
        onPostClick = { post ->
          navHostController.navigate(Screen.PostDetail.createRoute(post.id))
        },
        onSettingsClick = {
          navHostController.navigate(Screen.Settings.route)
        },
        onFABClick = {
          navHostController.navigate(Screen.AddPost.route)
        },
        onNavigateToLogin = {
          navHostController.navigate(Screen.Login.route)
        },
        onNavigateToAccountManagement = {
          navHostController.navigate(Screen.Account.route)
        }
      )
    }
    composable(route = Screen.AddPost.route) {
      AddScreen(
        onBackClick = { navHostController.navigateUp() },
        onSaveClick = { navHostController.navigateUp() }
      )
    }
    composable(route = Screen.Settings.route) {
      SettingsScreen(
        onBackClick = { navHostController.navigateUp() }
      )
    }
    composable(route = Screen.Login.route) {
      LogScreen(
        onHomeFeedNav = { navHostController.navigate(Screen.Homefeed.route) }
      )
    }

    composable(route = Screen.Account.route) {
      AccountScreen(
        onHomeFeedNav = { navHostController.navigate(Screen.Homefeed.route) },
        onAuthenticationNeeded = { navHostController.navigate(Screen.Login.route)}
      )
    }

    composable(
      route = Screen.PostDetail.route,
      arguments = Screen.PostDetail.navArguments,
    ) {
      PostDetailScreen(
        postId = it.arguments?.getString("postId") ?: "",
        onBackClick = { navHostController.navigateUp() },
        onFABClick = { navHostController.navigate(Screen.AddComment.route) }
      )
    }

    composable(route = Screen.AddComment.route) {
      AddCommentScreen(
        onBackClick = { navHostController.navigateUp() }
      )
    }
  }
}
