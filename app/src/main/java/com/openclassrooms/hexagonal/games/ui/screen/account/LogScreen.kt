package com.openclassrooms.hexagonal.games.ui.screen.account

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.firebase.ui.auth.AuthState
import com.firebase.ui.auth.configuration.PasswordRule
import com.firebase.ui.auth.configuration.authUIConfiguration
import com.firebase.ui.auth.configuration.auth_provider.AuthProvider
import com.firebase.ui.auth.ui.screens.FirebaseAuthScreen
import com.openclassrooms.hexagonal.games.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(onHomeFeedNav: () -> Unit) {
    //TODO Adapté les écrans au cahier des charges
    val context = LocalContext.current
    val configuration = authUIConfiguration {
        this.context = context
        providers {
            provider(
                AuthProvider.Email(
                    emailLinkActionCodeSettings = null,
                    passwordValidationRules = listOf(
                        PasswordRule.MinimumLength(6),
                        PasswordRule.RequireDigit,
                        PasswordRule.RequireLowercase,
                    ),
                    minimumPasswordLength = 6,
                )
            )
        }
        isMfaEnabled = false
    }
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Login") },
            navigationIcon = {
                IconButton(onClick = { onHomeFeedNav() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        )
    }) { innerPadding ->
        FirebaseAuthScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            configuration = configuration,
            onSignInSuccess = { _ ->
                onHomeFeedNav()
            },
            onSignInFailure = {
                Log.d("Firebase", "onSignInFailure: $it")
                Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show()
            },
            onSignInCancelled = {
                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
                onHomeFeedNav()
            },
            authenticatedContent = { state, _ ->
                val user = when (state) {
                    is AuthState.Success -> state.user
                    is AuthState.RequiresEmailVerification -> state.user
                    else -> null
                }

                if (user != null) {
                    ProfileGuard(user.uid) {
                        LaunchedEffect(Unit) {
                            onHomeFeedNav()
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }


            }

        )
    }

}

@Composable
fun ProfileGuard(
    uid: String,
    viewModel: LogViewModel = hiltViewModel(),
    onUserReady: @Composable () -> Unit
) {
    val userExists by viewModel.userExists.collectAsState()

    LaunchedEffect(uid) {
        viewModel.checkUser(uid)
    }

    when (userExists) {
        null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        false -> {
            ProfileCompletionScreen(
                onSave = { firstname, lastname ->
                    viewModel.createUser(User(uid, firstname, lastname))
                }
            )
        }

        true -> {
            onUserReady()
        }

    }


}

@Composable
fun ProfileCompletionScreen(onSave: (String, String) -> Unit) {
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = firstname,
            onValueChange = { firstname = it },
            label = { Text("Prénom") },
            modifier = Modifier.padding(16.dp)
        )
        OutlinedTextField(
            value = lastname,
            onValueChange = { lastname = it },
            label = { Text("Nom") },
            modifier = Modifier.padding(16.dp)
        )
        Button(
            onClick = { onSave(firstname, lastname) },
            enabled = firstname.isNotBlank() && lastname.isNotBlank(),
        ) {
            Text("Enregistrer")
        }
    }


}