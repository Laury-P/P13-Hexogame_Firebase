package com.openclassrooms.hexagonal.games.ui.screen.postDetail

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PostDetailScreen(
    postId: String,
){
    Text(text = "Detail du post $postId")

}