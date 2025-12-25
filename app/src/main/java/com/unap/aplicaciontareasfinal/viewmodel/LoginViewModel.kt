package com.unap.aplicaciontareasfinal.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unap.aplicaciontareasfinal.data.LoginRequest
import com.unap.aplicaciontareasfinal.data.User
import com.unap.aplicaciontareasfinal.datastore.UserDataStore
import com.unap.aplicaciontareasfinal.network.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userService: UserService,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, contrasenia: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val response = userService.login(LoginRequest(email, contrasenia))
            if (response != null) {
                val user = User(
                    id = response.id,
                    nombre = response.nombre,
                    email = response.email,
                    numeroWhatsapp = response.numeroWhatsapp,
                    date = response.date
                )
                userDataStore.saveUser(user)
                Log.d("LoginViewModel", "User saved with ID: ${user.id}")
                _loginState.value = LoginState.Success
            } else {
                _loginState.value = LoginState.Error("Invalid credentials")
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
