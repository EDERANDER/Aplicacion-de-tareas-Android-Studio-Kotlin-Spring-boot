package com.unap.aplicaciontareasfinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unap.aplicaciontareasfinal.datastore.UserDataStore
import com.unap.aplicaciontareasfinal.network.UserService
import com.unap.aplicaciontareasfinal.network.TaskService // Importar TaskService
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

sealed class AllTasksDeletionState {
    object Idle : AllTasksDeletionState()
    object Loading : AllTasksDeletionState()
    object Success : AllTasksDeletionState()
    data class Error(val message: String) : AllTasksDeletionState()
}

class ProfileViewModel(
    private val userDataStore: UserDataStore,
    private val userService: UserService,
    private val taskService: TaskService // Añadir TaskService como dependencia
) : ViewModel() {

    private val _userDeletionState = MutableStateFlow<UserDeletionState>(UserDeletionState.Idle)
    val userDeletionState: StateFlow<UserDeletionState> = _userDeletionState.asStateFlow()

    private val _allTasksDeletionState = MutableStateFlow<AllTasksDeletionState>(AllTasksDeletionState.Idle)
    val allTasksDeletionState: StateFlow<AllTasksDeletionState> = _allTasksDeletionState.asStateFlow()

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

    fun deleteAllUserTasks() {
        viewModelScope.launch {
            _allTasksDeletionState.value = AllTasksDeletionState.Loading
            val user = userDataStore.getUser.first()
            if (user != null) {
                val success = taskService.deleteAllTasks(user.id)
                if (success) {
                    _allTasksDeletionState.value = AllTasksDeletionState.Success
                } else {
                    _allTasksDeletionState.value = AllTasksDeletionState.Error("Error al eliminar todas las tareas.")
                }
            } else {
                _allTasksDeletionState.value = AllTasksDeletionState.Error("Usuario no encontrado.")
            }
        }
    }
}
