package com.example.chatgptapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Login)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthViewModel", "Login bem-sucedido")
                    _authState.value = AuthState.LoggedIn
                } else {
                    Log.d("AuthViewModel", "Falha no login: ${task.exception?.message}")
                    _authState.value = AuthState.Login
                }
            }
        }
    }
    fun register(nome: String, apelido: String, email: String, password: String) {
        viewModelScope.launch {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Usuário criado com sucesso
                        val user = firebaseAuth.currentUser
                        val profileUpdates = userProfileChangeRequest {
                            displayName = "$nome $apelido"
                        }
                        user?.updateProfile(profileUpdates)?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Log.d("AuthViewModel", "Perfil do usuário atualizado.")
                            }
                        }
                        _authState.value = AuthState.LoggedIn
                        Log.d("AuthViewModel", "Registro concluído")
                    } else {
                        // Falha ao criar o usuário
                        Log.d("AuthViewModel", "Falha no registro: ${task.exception?.message}")
                        _authState.value = AuthState.Register
                    }
                }
        }
    }

    fun navigateToRegister() {
        _authState.value = AuthState.Register
    }
    fun navigateToLogin() {
        _authState.value = AuthState.Login
    }

    fun logout() {
        firebaseAuth.signOut()
        _authState.value = AuthState.Login
        Log.d("AuthViewModel", "Logout efetuado")
    }

}

sealed class AuthState {
    object Login : AuthState()
    object Register : AuthState()
    object LoggedIn : AuthState()
}


