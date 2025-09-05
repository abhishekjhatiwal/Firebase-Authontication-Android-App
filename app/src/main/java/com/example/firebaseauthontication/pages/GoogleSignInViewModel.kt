package com.example.firebaseauthontication.pages

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class GoogleSignInViewModel(
    private val googleAuthUiClient: GoogleAuthUiClient
) : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    init {
        // Check if user is already signed in when ViewModel is created
        checkCurrentUser()
    }

    /**
     * Initiates the Google sign-in process
     */
    fun signIn() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, signInError = null) }

            try {
                val intentSender = googleAuthUiClient.signIn()
                if (intentSender == null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            signInError = "Failed to initiate sign-in process"
                        )
                    }
                }
                // Note: The actual sign-in completion will be handled in onSignInResult
                // after the user completes the Google sign-in flow
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        signInError = "Sign-in initialization failed: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Handles the result from Google sign-in intent
     */
    fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage,
                isLoading = false,
                userData = result.data
            )
        }
    }

    /**
     * Signs out the current user
     */
    fun signOut() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, signInError = null) }

            try {
                googleAuthUiClient.signOut()
                _state.update {
                    SignInState() // Reset to initial state
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        signInError = "Sign out failed: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Resets the entire state to initial values
     */
    fun resetState() {
        _state.update { SignInState() }
    }

    /**
     * Clears any existing error message
     */
    fun clearError() {
        _state.update { it.copy(signInError = null) }
    }

    /**
     * Sets loading state manually (useful for UI feedback)
     */
    fun setLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    /**
     * Checks if there's a currently signed-in user and updates state accordingly
     */
    private fun checkCurrentUser() {
        val currentUser = googleAuthUiClient.getSignedInUser()
        if (currentUser != null) {
            _state.update {
                it.copy(
                    isSignInSuccessful = true,
                    userData = currentUser,
                    isLoading = false
                )
            }
        }
    }

    /**
     * Refreshes current user data
     */
    fun refreshUserData() {
        checkCurrentUser()
    }
}


/**
 * Factory class for creating GoogleSignInViewModel with dependencies
 */
class GoogleSignInViewModelFactory(
    private val googleAuthUiClient: GoogleAuthUiClient
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoogleSignInViewModel::class.java)) {
            return GoogleSignInViewModel(googleAuthUiClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
































/*
class GoogleSignInViewModel: ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()
    fun onSignInResult(result: SignInResult) {
        _state.update { it.copy(
            signInError = result.errorMessage,
        ) }
    }

    fun resetState(){
        _state.update { SignInState() }
    }
}

 */