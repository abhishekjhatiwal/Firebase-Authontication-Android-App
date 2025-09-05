package com.example.firebaseauthontication

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> get() = _authState

    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> get() = _currentUser

    init {
        checkUserAuthentication()
        // Listen to auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
            if (firebaseAuth.currentUser != null) {
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value = AuthState.UnAuthenticated
            }
        }
    }

    fun checkUserAuthentication() {
        val user = auth.currentUser
        _currentUser.value = user
        if (user != null) {
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.UnAuthenticated
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                if (result.user != null) {
                    _authState.value = AuthState.Authenticated
                    _currentUser.value = result.user
                } else {
                    _authState.value = AuthState.Error("Login failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun signup(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                if (result.user != null) {
                    _authState.value = AuthState.Authenticated
                    _currentUser.value = result.user
                } else {
                    _authState.value = AuthState.Error("Signup failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Signup failed")
            }
        }
    }

    /**
     * Sign in with Google credentials (called after Google sign-in is successful)
     */
    fun signInWithGoogleCredential(credential: AuthCredential) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val result = auth.signInWithCredential(credential).await()
                if (result.user != null) {
                    _authState.value = AuthState.Authenticated
                    _currentUser.value = result.user
                } else {
                    _authState.value = AuthState.Error("Google sign-in failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Google sign-in failed")
            }
        }
    }

    /**
     * Link Google account with existing Firebase account
     */
    fun linkWithGoogleCredential(credential: AuthCredential) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _authState.value = AuthState.Error("No user is currently signed in")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val result = currentUser.linkWithCredential(credential).await()
                if (result.user != null) {
                    _authState.value = AuthState.Authenticated
                    _currentUser.value = result.user
                } else {
                    _authState.value = AuthState.Error("Account linking failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Account linking failed")
            }
        }
    }

    fun signout() {
        viewModelScope.launch {
            try {
                auth.signOut()
                _authState.value = AuthState.UnAuthenticated
                _currentUser.value = null
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Sign out failed: ${e.message}")
            }
        }
    }

    /**
     * Clear any error state
     */
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.UnAuthenticated
        }
    }

    /**
     * Refresh current user data
     */
    fun refreshUser() {
        viewModelScope.launch {
            try {
                auth.currentUser?.reload()?.await()
                _currentUser.value = auth.currentUser
            } catch (e: Exception) {
                // Handle refresh error if needed
            }
        }
    }

    /**
     * Get current Firebase user
     */
    fun getCurrentFirebaseUser(): FirebaseUser? = auth.currentUser

    /**
     * Check if user is signed in with Google
     */
    fun isSignedInWithGoogle(): Boolean {
        return auth.currentUser?.providerData?.any {
            it.providerId == "google.com"
        } ?: false
    }

    /**
     * Check if user is signed in with email/password
     */
    fun isSignedInWithEmailPassword(): Boolean {
        return auth.currentUser?.providerData?.any {
            it.providerId == "password"
        } ?: false
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object UnAuthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * Extension function to get user display info
 */
fun FirebaseUser.toUserDisplayInfo(): UserDisplayInfo {
    return UserDisplayInfo(
        uid = this.uid,
        email = this.email,
        displayName = this.displayName,
        photoUrl = this.photoUrl?.toString(),
        isEmailVerified = this.isEmailVerified,
        providers = this.providerData.map { it.providerId }
    )
}

/**
 * Data class for user display information
 */
data class UserDisplayInfo(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean,
    val providers: List<String>
)











































/*
class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> get() = _authState

    init {
        checkUserAuthentication()
    }

    fun checkUserAuthentication() {
        if (auth.currentUser != null) {
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.UnAuthenticated
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Login failed")
                }
            }
    }

    fun signup(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Signup failed")
                }
            }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.UnAuthenticated
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object UnAuthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}


 */















































































