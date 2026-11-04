package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.ui.navigation.AppNavigation
import com.example.ui.screens.SignInScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Attempt silent auto-sign in on cold start
        viewModel.authManager.attemptAutoSignIn(
            onSuccess = { user ->
                viewModel.loadUserData(
                    uid = user.uid,
                    name = user.displayName ?: "ব্যবহারকারী",
                    email = user.email ?: "",
                    photoUrl = user.photoUrl?.toString() ?: ""
                )
            },
            onUnauthenticated = {
                // Stay on login screen
            },
            scope = lifecycleScope
        )

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

                    DisposableEffect(Unit) {
                        val listener = FirebaseAuth.AuthStateListener { auth ->
                            currentUser = auth.currentUser
                        }
                        Firebase.auth.addAuthStateListener(listener)
                        onDispose {
                            Firebase.auth.removeAuthStateListener(listener)
                        }
                    }

                    if (currentUser == null) {
                        SignInScreen(
                            authManager = viewModel.authManager,
                            onSignInSuccess = {
                                val user = Firebase.auth.currentUser
                                if (user != null) {
                                    viewModel.loadUserData(
                                        uid = user.uid,
                                        name = user.displayName ?: "ব্যবহারকারী",
                                        email = user.email ?: "",
                                        photoUrl = user.photoUrl?.toString() ?: ""
                                    )
                                }
                            }
                        )
                    } else {
                        AppNavigation(
                            viewModel = viewModel,
                            onSignOut = {
                                currentUser = null
                            }
                        )
                    }
                }
            }
        }
    }
}
