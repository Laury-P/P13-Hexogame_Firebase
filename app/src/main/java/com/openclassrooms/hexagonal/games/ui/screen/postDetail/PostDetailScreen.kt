package com.openclassrooms.hexagonal.games.ui.screen.postDetail

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.imageLoader
import coil.util.DebugLogger
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.model.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    viewModel: PostDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val post by viewModel.post.collectAsState()
    viewModel.loadPost(postId)

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.contentDescription_go_back)
                        )
                    }
                },
                title = {
                    Text(text = post?.title ?: "")
                },
                )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.description_button_add)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End

    ) { contentPadding ->
        PostDetailContent(
            modifier = Modifier.padding(contentPadding),
            post = post
        )


    }


}

@Composable
private fun PostDetailContent(
    modifier: Modifier = Modifier,
    post: Post?
) {
    LazyColumn(modifier = modifier.padding( 8.dp)) {
        item {
            Text(
                text = stringResource(
                    R.string.by,
                    post?.author?.firstname ?: "",
                    post?.author?.lastname ?: ""
                )
            )
        }
        item {
            Text(text = post?.title?: "", style = MaterialTheme.typography.titleLarge)
        }
        if (!post?.description.isNullOrEmpty()) {
            item {
                Text(
                    text = post.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        if(!post?.photoUrl.isNullOrEmpty()) {
            item {
                AsyncImage(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .aspectRatio(ratio = 16 / 9f),
                    model = post.photoUrl,
                    imageLoader = LocalContext.current.imageLoader.newBuilder()
                        .logger(DebugLogger())
                        .build(),
                    placeholder = ColorPainter(Color.DarkGray),
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}