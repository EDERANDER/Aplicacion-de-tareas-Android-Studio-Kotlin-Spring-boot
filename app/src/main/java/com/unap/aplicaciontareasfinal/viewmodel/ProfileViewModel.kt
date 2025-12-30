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

/**
 * Representa los diferentes estados posibles de la operacion de eliminacion de usuario.
 */
sealed class UserDeletionState {
    object Idle : UserDeletionState()
    object Loading : UserDeletionState()
    object Success : UserDeletionState()
    data class Error(val message: String) : UserDeletionState()
}

/**
 * Representa los diferentes estados posibles de la operacion de eliminacion de todas las tareas.
 */
sealed class AllTasksDeletionState {
    object Idle : AllTasksDeletionState()
    object Loading : AllTasksDeletionState()
    object Success : AllTasksDeletionState()
    data class Error(val message: String) : AllTasksDeletionState()
}

/**
 * Este ViewModel maneja la logica y el estado de la pantalla de Perfil.
 * Se encarga de operaciones "peligrosas" como eliminar la cuenta del usuario
 * o todas sus tareas.
 *
 * @param userDataStore Para obtener el ID del usuario y para limpiar los datos locales.
 * @param userService Para realizar la llamada de red para eliminar al usuario.
 * @param taskService Para realizar la llamada de red para eliminar todas las tareas.
 */
class ProfileViewModel(
    private val userDataStore: UserDataStore,
    private val userService: UserService,
    private val taskService: TaskService // Añadir TaskService como dependencia
) : ViewModel() {

    // Flujo de estado para la operacion de eliminacion de cuenta.
    private val _userDeletionState = MutableStateFlow<UserDeletionState>(UserDeletionState.Idle)
    val userDeletionState: StateFlow<UserDeletionState> = _userDeletionState.asStateFlow()

    // Flujo de estado para la operacion de eliminacion de todas las tareas.
    private val _allTasksDeletionState = MutableStateFlow<AllTasksDeletionState>(AllTasksDeletionState.Idle)
    val allTasksDeletionState: StateFlow<AllTasksDeletionState> = _allTasksDeletionState.asStateFlow()

    /**
     * Inicia el proceso para eliminar permanentemente la cuenta del usuario actual.
     */
    fun deleteCurrentUser() {
        viewModelScope.launch {
            _userDeletionState.value = UserDeletionState.Loading
            // .first() obtiene el primer valor emitido por el Flow y luego lo cancela.
            val user = userDataStore.getUser.first()
            if (user != null) {
                val success = userService.deleteUser(user.id)
                if (success) {
                    // Si la eliminacion en el backend es exitosa, se limpian los datos locales.
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

    /**
     * Inicia el proceso para eliminar permanentemente todas las tareas del usuario actual.
     */
    fun deleteAllUserTasks() {
        viewModelScope.launch {
            _allTasksDeletionState.value = AllTasksDeletionState.Loading
            val user = userDataStore.getUser.first()
            if (user != null) {
                val success = taskService.deleteAllTasks(user.id)
                if (success) {
                    _allTasksDeletionState.value = AllTasksDeletionState.Success
                    // Nota: aqui podriamos tambien forzar la actualizacion de la lista de tareas
                    // en TaskViewModel si fuera necesario, para que la UI principal se actualice.
                } else {
                    _allTasksDeletionState.value = AllTasksDeletionState.Error("Error al eliminar todas las tareas.")
                }
            } else {
                _allTasksDeletionState.value = AllTasksDeletionState.Error("Usuario no encontrado.")
            }
        }
    }
}
