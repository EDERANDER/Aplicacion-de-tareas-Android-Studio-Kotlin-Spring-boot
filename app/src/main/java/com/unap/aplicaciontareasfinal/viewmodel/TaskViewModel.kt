package com.unap.aplicaciontareasfinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unap.aplicaciontareasfinal.datastore.UserDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState

    fun logout() {
        viewModelScope.launch {
            userDataStore.clearData()
            _logoutState.value = LogoutState.Success
        }
    }
}

sealed class LogoutState {
    object Idle : LogoutState()
    object Success : LogoutState()
}
