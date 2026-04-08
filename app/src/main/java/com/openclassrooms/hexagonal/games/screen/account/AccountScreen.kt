package com.openclassrooms.hexagonal.games.screen.account


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun AccountScreen  (viewModel: AccountViewModel = hiltViewModel(), onHomeFeedNav: () -> Unit) {
    val context = LocalContext.current
    Button(
        onClick = {
            viewModel.logout(context)
            onHomeFeedNav()
        },
        modifier = Modifier.padding(50.dp)
    ) {
        Text("logout")
    }

}