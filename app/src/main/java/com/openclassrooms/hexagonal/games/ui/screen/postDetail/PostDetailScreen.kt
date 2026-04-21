package com.openclassrooms.hexagonal.games.ui.screen.postDetail

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.LocalAuthState
import com.openclassrooms.hexagonal.games.domain.model.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    viewModel: PostDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onFABClick: () -> Unit = {}
) {
    val post by viewModel.post.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val logState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    viewModel.loadPost(postId)

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
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
            FloatingActionButton(onClick = {
                if (logState is LocalAuthState.LoggedIn) {
                    onFABClick()
                } else {
                    Toast.makeText(
                        context,
                        R.string.loggedin_to_comment,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
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
            post = post,
            comments = comments
        )
    }
}

@Composable
private fun PostDetailContent(
    modifier: Modifier = Modifier,
    post: Post?,
    comments: List<Comment>
) {
    LazyColumn(modifier = modifier.padding(8.dp)) {
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
            Text(text = post?.title ?: "", style = MaterialTheme.typography.titleLarge)
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
        if (!post?.photoUrl.isNullOrEmpty()) {
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

        item {
            Text(
                text = stringResource(id = R.string.comments),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        if (comments.isEmpty()) {
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(text = stringResource(id = R.string.no_comments))
            }
        } else {
            items(comments) { comment ->
                CommentCell(comment = comment)
            }
        }


    }

}

@Composable
fun CommentCell(
    comment: Comment
) {
    HorizontalDivider(
        modifier = Modifier.padding(top = 8.dp)
    )
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = stringResource(
                id = R.string.by,
                comment.author?.firstname ?: "",
                comment.author?.lastname ?: ""
            ),
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = comment.content,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }

}