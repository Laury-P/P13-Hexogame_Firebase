package com.openclassrooms.hexagonal.games.ui.screen.postDetail

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PostDetailScreen(
    postId: String,
    viewModel: PostDetailViewModel = hiltViewModel()
){
    val post by viewModel.post.collectAsState()
    viewModel.loadPost(postId)

    Column{
        Text(text = post?.author?.firstname ?: "")
        Text(text = post?.author?.lastname ?: "")
        Text(text = post?.title ?: "")
        Text(text = post?.description ?: "")
    }





}