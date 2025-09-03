package com.example.firebaseauthontication.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.firebaseauthontication.AuthState
import com.example.firebaseauthontication.AuthViewModel

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState = authViewModel.authState.observeAsState()

//    LaunchedEffect(authState.value) {
//        when (authState.value) {
//            is AuthState.UnAuthenticated -> navController.navigate("login")
//            else -> Unit
//        }
//    }
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.UnAuthenticated -> {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true } // clear Home from back stack
                    launchSingleTop = true
                }
            }
            else -> Unit
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text="Home Page", fontSize = 30.sp)
        TextButton(onClick = {
            authViewModel.signout()
        }) {
            Text(text = "Logout")
        }
    }
}