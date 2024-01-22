package com.example.chatgptapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Login)
    val isLoggedIn: Boolean
        get() = authState.value is AuthState.LoggedIn
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    private val userEmail = MutableStateFlow("")
    private val userPassword = MutableStateFlow("")

    fun login(email: String, password: String) {
        viewModelScope.launch {
            if (email == "utilizador@gmail.com" && password == "password1") {
                Log.d("AuthViewModel", "Login bem-sucedido")
                _authState.value = AuthState.LoggedIn
            } else {
                Log.d("AuthViewModel", "Falha no login")
                _authState.value = AuthState.Login
            }
        }
    }


    fun register(email: String, password: String) {
        viewModelScope.launch {
            userEmail.value = email
            userPassword.value = password
            _authState.value = AuthState.Login
        }
    }


}

sealed class AuthState {
    object Login : AuthState()
    object Register : AuthState()
    object LoggedIn : AuthState()
}


