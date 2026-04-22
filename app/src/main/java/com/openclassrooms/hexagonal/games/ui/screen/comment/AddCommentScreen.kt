package com.openclassrooms.hexagonal.games.ui.screen.comment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.hexagonal.games.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCommentScreen(
    postId: String,
    modifier: Modifier = Modifier,
    viewModel: AddCommentViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
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
                onClick = { viewModel.addComment() },
                enabled = !error
            ){
                Text(text = stringResource(id = R.string.action_save))
            }

        }

    }

}