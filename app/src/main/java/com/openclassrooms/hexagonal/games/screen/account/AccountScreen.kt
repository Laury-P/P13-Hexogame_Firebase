package com.openclassrooms.hexagonal.games.screen.account


import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.hexagonal.games.ui.event.AccountEvent


@Composable
fun AccountScreen  (
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
                    Toast.makeText(context, "Network error, check your internet connection and retry", Toast.LENGTH_SHORT).show()
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

    Scaffold() { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            Button(
                onClick = {
                    viewModel.logout(context)
                    onHomeFeedNav()
                },
                modifier = Modifier.padding(12.dp)
            ) {
                Text("logout")
            }

            Button(
                onClick = {
                    viewModel.deleteAccount(context)
                },
                modifier = Modifier.padding(12.dp)
            ){
                Text("deleteAccount")
            }
        }

    }


}