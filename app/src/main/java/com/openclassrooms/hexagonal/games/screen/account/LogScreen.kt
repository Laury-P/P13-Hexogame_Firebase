package com.openclassrooms.hexagonal.games.screen.account

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.firebase.ui.auth.AuthState
import com.firebase.ui.auth.configuration.PasswordRule
import com.firebase.ui.auth.configuration.authUIConfiguration
import com.firebase.ui.auth.configuration.auth_provider.AuthProvider
import com.firebase.ui.auth.ui.screens.FirebaseAuthScreen

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

    FirebaseAuthScreen(
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
            LaunchedEffect(state) {
                when (state) {
                    is AuthState.Success,
                    is AuthState.RequiresEmailVerification -> {
                        onHomeFeedNav()
                    }
                    else -> Unit
                }
            }

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

    )
}