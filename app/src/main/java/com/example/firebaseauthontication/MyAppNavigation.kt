package com.example.firebaseauthontication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.firebaseauthontication.pages.HomePage
import com.example.firebaseauthontication.pages.LoginPage
import com.example.firebaseauthontication.pages.SignUpPage
import androidx.compose.runtime.livedata.observeAsState
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext


@Composable
fun MyPageNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true } // removes login from backstack
                    launchSingleTop = true
                }
            }
            is AuthState.UnAuthenticated,
            null -> {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true } // removes home from backstack
                    launchSingleTop = true
                }
            }
            is AuthState.Loading -> {
                Toast.makeText(context, "Please wait...", Toast.LENGTH_SHORT).show()
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_LONG).show()
            }
        }
    }


    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginPage(modifier, navController, authViewModel)
        }
        composable("signup") {
            SignUpPage(modifier, navController, authViewModel)
        }
        composable("home") {
            HomePage(modifier, navController, authViewModel)
        }
    }
}























/*
@Composable
fun MyPageNavigation(modifier:Modifier= Modifier,authViewModel: AuthViewModel){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login"){
            LoginPage(modifier,navController,authViewModel)
        }
        composable("signup"){
            SignUpPage(modifier,navController,authViewModel)
        }
        composable("home"){
            HomePage(modifier,navController,authViewModel)
        }
    })
}

 */