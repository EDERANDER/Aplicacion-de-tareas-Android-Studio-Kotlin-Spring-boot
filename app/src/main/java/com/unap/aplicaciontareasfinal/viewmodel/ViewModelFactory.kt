package com.unap.aplicaciontareasfinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.unap.aplicaciontareasfinal.datastore.UserDataStore
import com.unap.aplicaciontareasfinal.network.UserService

class ViewModelFactory(
    private val userDataStore: UserDataStore,
    private val userService: UserService? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userService!!, userDataStore) as T
        }
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(userDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
