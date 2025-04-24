package com.example.grocerywise

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(
        email: String,
        password: String,
    ) {
        if (email.isEmpty()) {
            _authState.value = AuthState.Error("Email cannot be empty")
            return
        }

        if (password.isEmpty()) {
            _authState.value = AuthState.Error("Password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Error logging in")
                }
            }
    }

    fun signup(
        email: String,
        password: String,
    ) {
        if (email.isEmpty()) {
            _authState.value = AuthState.Error("Email cannot be empty")
            return
        }

        if (password.isEmpty()) {
            _authState.value = AuthState.Error("Password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Error creating account")
                }
            }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    /**
     * Send a password reset email to [email].
     * Calls [onResult] with (success, errorMessage).
     */
    fun resetPassword(email: String, onResult: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Log.d("AuthVM", "✅ sendPasswordResetEmail: SUCCESS")
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                Log.e("AuthVM", "❌ sendPasswordResetEmail: FAILED — ${e.message}")
                onResult(false, e.message)
            }
    }


}

sealed class AuthState {
    object Authenticated : AuthState()

    object Unauthenticated : AuthState()

    object Loading : AuthState()

    data class Error(
        val message: String,
    ) : AuthState()
}
