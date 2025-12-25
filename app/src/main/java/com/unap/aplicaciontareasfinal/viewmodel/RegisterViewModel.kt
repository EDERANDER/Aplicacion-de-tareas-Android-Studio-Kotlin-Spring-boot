package com.unap.aplicaciontareasfinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unap.aplicaciontareasfinal.data.RegisterRequest
import com.unap.aplicaciontareasfinal.network.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val userService: UserService
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun register(nombre: String, email: String, numeroWhatsapp: String, contraseña: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            val response = userService.registerUser(RegisterRequest(nombre, email, numeroWhatsapp, contraseña))
            if (response != null) {
                _registerState.value = RegisterState.Success
            } else {
                _registerState.value = RegisterState.Error("Registration failed")
            }
        }
    }
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}
