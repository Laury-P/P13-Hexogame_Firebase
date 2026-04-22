package com.openclassrooms.hexagonal.games.ui.screen.comment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.util.IsPublishing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCommentScreen(
    postId: String,
    modifier: Modifier = Modifier,
    viewModel: AddCommentViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
    val isPublishing by viewModel.isPublishing.collectAsState()

    LaunchedEffect(isPublishing) {
        if (isPublishing is IsPublishing.Published) {
            onBackClick()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.add_comment_label))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onBackClick()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.contentDescription_go_back)
                        )
                    }
                }
            )
        }) { contentPadding ->
        val content by viewModel.content.collectAsState()
        val error by viewModel.error.collectAsState()

        if (isPublishing is IsPublishing.Publishing) {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }
        }


        Column(modifier = Modifier.padding(contentPadding)) {
            OutlinedTextField(
                modifier = Modifier.padding(16.dp),
                value = content,
                onValueChange = { viewModel.onContentChanged(it) },
                label = { Text(stringResource(id = R.string.comments)) },
                isError = error,
                supportingText = {
                    if (error) {
                        Text(text = stringResource(id = R.string.error_comment))
                    }
                }
            )
            Button(
                modifier = Modifier.padding(16.dp),
                onClick = { viewModel.addComment(postId) },
                enabled = !error && isPublishing !is IsPublishing.Publishing
            ){
                Text(text = stringResource(id = R.string.action_save))
            }

        }

    }

}