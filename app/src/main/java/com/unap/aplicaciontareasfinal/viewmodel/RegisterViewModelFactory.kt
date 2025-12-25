package com.unap.aplicaciontareasfinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.unap.aplicaciontareasfinal.network.UserService

class RegisterViewModelFactory(
    private val userService: UserService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(userService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
