package com.example.firebaseauthontication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.firebaseauthontication.pages.GoogleAuthUiClient
import com.example.firebaseauthontication.ui.theme.FirebaseAuthonticationTheme
import com.google.android.gms.auth.api.identity.Identity

class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel: AuthViewModel by viewModels()
        setContent {
            FirebaseAuthonticationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyPageNavigation(modifier=Modifier.padding(innerPadding), authViewModel, googleAuthUiClient)
                }
            }
        }
    }
}
