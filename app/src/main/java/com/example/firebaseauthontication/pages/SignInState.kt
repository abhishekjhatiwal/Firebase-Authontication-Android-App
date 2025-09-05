package com.example.firebaseauthontication.pages

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val isLoading: Boolean = false,
    val userData: UserData? = null
) {
    val hasError: Boolean get() = signInError != null
    val isSignedIn: Boolean get() = isSignInSuccessful && userData != null
}
