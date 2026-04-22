package com.openclassrooms.hexagonal.games.ui.screen

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val navArguments: List<NamedNavArgument> = emptyList()
) {
    data object Homefeed : Screen("homefeed")

    data object AddPost : Screen("addPost")

    data object Settings : Screen("settings")

    data object Login : Screen("login")

    data object Account : Screen("account")

    data object PostDetail : Screen(
        route = "postDetail/{postId}",
        navArguments = listOf(navArgument("postId") { type = NavType.StringType })
    ){
        fun createRoute(postId: String) = "postDetail/$postId"
    }

    data object AddComment : Screen(
        route ="AddComment/{postId}",
        navArguments = listOf(navArgument("postId") { type = NavType.StringType })
    ){
        fun createRoute(postId: String) = "AddComment/$postId"
    }

}

