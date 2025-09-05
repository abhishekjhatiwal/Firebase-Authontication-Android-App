package com.example.firebaseauthontication.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun SignInScreen(
    state: SignInState,
    onSignInClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sign in with Google",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )
            Text("Signing in...")
        } else {
            Button(
                onClick = onSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Continue with Google")
            }
        }

        state.signInError?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

























//@Composable
//fun SignInScreen(
//    state: SignInState,
//    onSignInClick: () -> Unit
//){
//    val context = LocalContext.current
//    LaunchedEffect(key1=state.signInError) {
//        state.signInError?.let {
//            Toast.makeText(
//                context,
//                it,
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }
//    Box(
//        modifier = Modifier.fillMaxSize().padding(16.dp),
//        contentAlignment = Alignment.Center
//    ){
//        Button(onClick = onSignInClick){
//            Text(text = "Sign In")
//        }
//    }
//}