package com.openclassrooms.hexagonal.games.screen.account


import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.event.AccountEvent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: AccountViewModel = hiltViewModel(),
    onHomeFeedNav: () -> Unit,
    onAuthenticationNeeded: () -> Unit
) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AccountEvent.NeedReauthentification -> {
                    onAuthenticationNeeded()
                }

                is AccountEvent.NetworkError -> {
                    Toast.makeText(
                        context,
                        "Network error, check your internet connection and retry",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is AccountEvent.UnknownError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                is AccountEvent.AccountDeleted -> {
                    Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                    onHomeFeedNav()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.account)) },
                navigationIcon = {
                    IconButton(onClick = { onHomeFeedNav() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            Button(
                onClick = {
                    viewModel.logout(context)
                    onHomeFeedNav()
                },
                modifier = Modifier.padding(12.dp).fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.signOutButton))
            }

            Button(
                onClick = {
                    viewModel.deleteAccount()
                },
                modifier = Modifier.padding(12.dp).fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.deleteAccountButton))
            }
        }

    }


}