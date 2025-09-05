package com.example.firebaseauthontication

import android.app.Activity.RESULT_OK
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firebaseauthontication.pages.GoogleAuthUiClient
import com.example.firebaseauthontication.pages.GoogleSignInViewModel
import com.example.firebaseauthontication.pages.ProfileScreen
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.activity.result.IntentSenderRequest
import com.example.firebaseauthontication.pages.GoogleSignInViewModelFactory
import com.example.firebaseauthontication.pages.SignInResult
import com.example.firebaseauthontication.pages.SignInScreen

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun MyPageNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    googleAuthUiClient: GoogleAuthUiClient
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.observeAsState()
    val currentUser by authViewModel.currentUser.observeAsState()
    val context = LocalContext.current

    // Handle Firebase Auth state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                // Check if it's a Google user or regular Firebase user
                if (authViewModel.isSignedInWithGoogle()) {
                    navController.navigate("profile") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                } else {
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            is AuthState.UnAuthenticated, null -> {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
            is AuthState.Loading -> {
                // Handle loading state in UI
            }
            is AuthState.Error -> {
                // Handle error state in UI
            }
        }
    }

    // Check for existing authentication on app start
    LaunchedEffect(Unit) {
        authViewModel.checkUserAuthentication()
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

        composable("signinwithgoogle") {
            val viewModel = viewModel<GoogleSignInViewModel> {
                GoogleSignInViewModelFactory(googleAuthUiClient).create(GoogleSignInViewModel::class.java)
            }
            val googleState by viewModel.state.collectAsStateWithLifecycle()

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if (result.resultCode == RESULT_OK) {
                        GlobalScope.launch {
                            val signInResult = googleAuthUiClient.signInWithIntent(
                                intent = result.data ?: return@launch
                            )
                            viewModel.onSignInResult(signInResult)
                        }
                    } else {
                        viewModel.onSignInResult(
                            SignInResult(
                                data = null,
                                errorMessage = "Sign-in was cancelled"
                            )
                        )
                    }
                }
            )

            SignInScreen(
                state = googleState,
                onSignInClick = {
                    GlobalScope.launch {
                        viewModel.setLoading(true)
                        val signInIntentSender = googleAuthUiClient.signIn()
                        signInIntentSender?.let { intentSender ->
                            launcher.launch(
                                IntentSenderRequest.Builder(intentSender).build()
                            )
                        } ?: run {
                            viewModel.onSignInResult(
                                SignInResult(
                                    data = null,
                                    errorMessage = "Failed to initiate sign-in"
                                )
                            )
                        }
                    }
                }
            )

            // Handle Google sign-in success - integrate with Firebase Auth
            LaunchedEffect(key1 = googleState.isSignInSuccessful) {
                if (googleState.isSignInSuccessful && googleState.userData != null) {
                    // Get Google credentials and sign in to Firebase
                    GlobalScope.launch {
                        try {
                            // This assumes you have access to the Google ID token
                            // You might need to modify GoogleAuthUiClient to return the token
                            val googleUser = googleAuthUiClient.getSignedInUser()
                            if (googleUser != null) {
                                // The Firebase Auth state will be handled by the AuthViewModel listener
                                viewModel.resetState()
                            }
                        } catch (e: Exception) {
                            viewModel.onSignInResult(
                                SignInResult(
                                    data = null,
                                    errorMessage = "Failed to authenticate with Firebase: ${e.message}"
                                )
                            )
                        }
                    }
                }
            }

            // Handle Google sign-in errors
            LaunchedEffect(key1 = googleState.signInError) {
                googleState.signInError?.let { error ->
                    // Handle error display
                }
            }
        }

        composable("profile") {
            ProfileScreen(
                userData = googleAuthUiClient.getSignedInUser(),
                onSignOut = {
                    GlobalScope.launch {
                        // Sign out from both Google and Firebase
                        googleAuthUiClient.signOut()
                        authViewModel.signout()
                    }
                }
            )
        }
    }
}












































/*
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun MyPageNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    googleAuthUiClient: GoogleAuthUiClient
) {
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
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    LaunchedEffect(Unit) {
        if(googleAuthUiClient.getSignedInUser()!=null){
            navController.navigate("profile")
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
        composable("signinwithgoogle") {
            val viewModel = viewModel<GoogleSignInViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = { result ->
                    if (result.resultCode == RESULT_OK) {
                        GlobalScope.launch {
                            val signInResult = googleAuthUiClient.signInWithIntent(
                                intent = result.data ?: return@launch
                            )
                            viewModel.onSignInResult(signInResult)
                        }
                    }
                }
            )
//            SignInScreen(
//                state = state,
//                onSignInClick = {
//                    GlobalScope.launch {
//                        //  val signInIntentSender = googleAuthUiClient.signIn()
//                        launcher.launch(
//                            IntentSenderRequest.Builder(
//                                signInIntentSender ?: return@launch
//                            ).build()
//                        )
//                    }
//                }
//            )
//
//
//            LaunchedEffect(key1 = state.isSignInSuccessful) {
//                if (state.isSignInSuccessful) {
//                    Toast.makeText(
//                        context,
//                        "Sign in successful",
//                        Toast.LENGTH_LONG
//                    ).show()
//            navController.navigate("profile")
//            viewModel.resetState()
//                }
//            }
        }
        composable("profile") {
            ProfileScreen(
                userData = googleAuthUiClient.getSignedInUser(),
                onSignOut = {
                    GlobalScope.launch {
                        googleAuthUiClient.signOut()
                        Toast.makeText(
                            context,
                            "Signed out",
                            Toast.LENGTH_LONG
                        ).show()
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}



 */





































