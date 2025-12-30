package com.unap.aplicaciontareasfinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unap.aplicaciontareasfinal.datastore.UserDataStore
import com.unap.aplicaciontareasfinal.network.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class UserDeletionState {
    object Idle : UserDeletionState()
    object Loading : UserDeletionState()
    object Success : UserDeletionState()
    data class Error(val message: String) : UserDeletionState()
}

class ProfileViewModel(
    private val userDataStore: UserDataStore,
    private val userService: UserService
) : ViewModel() {

    private val _userDeletionState = MutableStateFlow<UserDeletionState>(UserDeletionState.Idle)
    val userDeletionState: StateFlow<UserDeletionState> = _userDeletionState.asStateFlow()

    fun deleteCurrentUser() {
        viewModelScope.launch {
            _userDeletionState.value = UserDeletionState.Loading
            val user = userDataStore.getUser.first()
            if (user != null) {
                val success = userService.deleteUser(user.id)
                if (success) {
                    userDataStore.clearData()
                    _userDeletionState.value = UserDeletionState.Success
                } else {
                    _userDeletionState.value = UserDeletionState.Error("Error al eliminar la cuenta.")
                }
            } else {
                _userDeletionState.value = UserDeletionState.Error("Usuario no encontrado.")
            }
        }
    }
}
