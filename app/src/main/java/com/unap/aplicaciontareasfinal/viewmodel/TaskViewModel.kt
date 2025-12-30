package com.unap.aplicaciontareasfinal.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unap.aplicaciontareasfinal.data.Task
import com.unap.aplicaciontareasfinal.data.TaskUpdateRequest
import com.unap.aplicaciontareasfinal.datastore.UserDataStore
import com.unap.aplicaciontareasfinal.network.TaskService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.unap.aplicaciontareasfinal.data.User // Importar la clase User

/**
 * Este es el ViewModel principal de la aplicacion. Se encarga de toda la logica de negocio
 * relacionada con las tareas: cargarlas, crearlas, actualizarlas y eliminarlas (CRUD).
 * Tambien maneja el estado de la UI para la pantalla de tareas y el cierre de sesion.
 *
 * @param userDataStore Para obtener el ID del usuario y limpiar los datos al cerrar sesion.
 * @param taskService Para realizar las llamadas de red relacionadas con las tareas.
 */
class TaskViewModel(
    private val userDataStore: UserDataStore,
    private val taskService: TaskService
) : ViewModel() {

    // Flujo de estado para la operacion de cierre de sesion.
    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState

    // Almacena la lista actual de tareas. La UI observa este flujo para mostrar las tareas.
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    // Controla si se esta realizando una operacion de carga de tareas en segundo plano.
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // Almacena un mensaje de error si alguna operacion falla. La UI puede mostrarlo en un Snackbar.
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Expone la informacion del usuario actual obtenida del DataStore.
    // `stateIn` convierte un Flow normal en un StateFlow, permitiendo que multiples
    // observadores compartan el mismo flujo de datos de forma eficiente.
    val user: StateFlow<User?> = userDataStore.getUser.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    /**
     * Cierra la sesion del usuario actual limpiando sus datos del DataStore.
     */
    fun logout() {
        viewModelScope.launch {
            userDataStore.clearData()
            _logoutState.value = LogoutState.Success
        }
    }

    /**
     * Carga la lista de tareas del usuario actual desde el servidor.
     */
    fun loadTasks() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val userId = userDataStore.getUser.first()?.id
                Log.d("TaskViewModel", "Attempting to load tasks for user ID: $userId")

                userId?.let {
                    val fetchedTasks = taskService.getTasksForUser(it)
                    _tasks.value = fetchedTasks
                    Log.d("TaskViewModel", "Fetched ${fetchedTasks.size} tasks.")
                } ?: run {
                    _error.value = "User ID not found. Please log in again."
                    Log.e("TaskViewModel", "User ID not found in DataStore.")
                }
            } catch (e: Exception) {
                _error.value = "Failed to load tasks: ${e.message}"
                Log.e("TaskViewModel", "Exception while loading tasks", e)
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Actualiza el estado (completada/pendiente) de una tarea especifica.
     * @param task La tarea a actualizar.
     * @param newStatus El nuevo estado (true para completada, false para pendiente).
     */
    fun updateTaskStatus(task: Task, newStatus: Boolean) {
        viewModelScope.launch {
            try {
                val userId = userDataStore.getUser.first()?.id
                if (userId == null) {
                    _error.value = "User ID not found."
                    return@launch
                }

                val taskUpdateRequest = TaskUpdateRequest(
                    titulo = task.titulo,
                    descripcion = task.descripcion,
                    recordatorio = task.recordatorio,
                    estado = newStatus
                )

                val success = taskService.updateTask(userId, task.id, taskUpdateRequest)

                if (success) {
                    // Si la actualizacion en el servidor es exitosa, actualiza la lista local.
                    val updatedTasks = _tasks.value.map {
                        if (it.id == task.id) {
                            it.copy(estado = newStatus)
                        } else {
                            it
                        }
                    }
                    _tasks.value = updatedTasks
                } else {
                    _error.value = "Failed to update task status."
                }
            } catch (e: Exception) {
                _error.value = "Error updating task: ${e.message}"
                Log.e("TaskViewModel", "Exception while updating task", e)
            }
        }
    }

    /**
     * Elimina una tarea especifica.
     * @param task La tarea a eliminar.
     */
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                val userId = userDataStore.getUser.first()?.id
                if (userId == null) {
                    _error.value = "User ID not found."
                    return@launch
                }

                val success = taskService.deleteTask(userId, task.id)

                if (success) {
                    // Si la eliminacion es exitosa, quita la tarea de la lista local.
                    _tasks.value = _tasks.value.filterNot { it.id == task.id }
                } else {
                    _error.value = "Failed to delete task."
                }
            } catch (e: Exception) {
                _error.value = "Error deleting task: ${e.message}"
                Log.e("TaskViewModel", "Exception while deleting task", e)
            }
        }
    }

    // Flujo de estado para las operaciones de crear o actualizar una tarea desde `AddTaskScreen`.
    private val _taskOperationState = MutableStateFlow<TaskOperationState>(TaskOperationState.Idle)
    val taskOperationState: StateFlow<TaskOperationState> = _taskOperationState

    /**
     * Crea una nueva tarea.
     * @param titulo El titulo de la tarea.
     * @param descripcion La descripcion de la tarea.
     * @param recordatorio La fecha y hora del recordatorio en formato String.
     */
    fun createTask(titulo: String, descripcion: String, recordatorio: String) {
        viewModelScope.launch {
            _taskOperationState.value = TaskOperationState.Loading
            try {
                val userId = userDataStore.getUser.first()?.id
                if (userId == null) {
                    _taskOperationState.value = TaskOperationState.Error("User ID not found.")
                    return@launch
                }

                val request = com.unap.aplicaciontareasfinal.data.TaskCreateRequest(
                    titulo = titulo,
                    descripcion = descripcion,
                    recordatorio = recordatorio,
                    estado = false // Las tareas nuevas siempre se crean como no completadas
                )

                val newTask = taskService.createTask(userId, request)

                if (newTask != null) {
                    _tasks.value = _tasks.value + newTask
                    _taskOperationState.value = TaskOperationState.Success
                } else {
                    _taskOperationState.value = TaskOperationState.Error("Failed to create task.")
                }
            } catch (e: Exception) {
                _taskOperationState.value = TaskOperationState.Error("Error creating task: ${e.message}")
                Log.e("TaskViewModel", "Exception while creating task", e)
            }
        }
    }

    /**
     * Actualiza todos los campos de una tarea existente.
     */
    fun updateTask(id: Int, titulo: String, descripcion: String, recordatorio: String, estado: Boolean) {
        viewModelScope.launch {
            _taskOperationState.value = TaskOperationState.Loading
            try {
                val userId = userDataStore.getUser.first()?.id
                if (userId == null) {
                    _taskOperationState.value = TaskOperationState.Error("User ID not found.")
                    return@launch
                }

                val request = TaskUpdateRequest(
                    titulo = titulo,
                    descripcion = descripcion,
                    recordatorio = recordatorio,
                    estado = estado
                )

                val success = taskService.updateTask(userId, id, request)

                if (success) {
                    // Actualiza la tarea en la lista local.
                    _tasks.value = _tasks.value.map {
                        if (it.id == id) {
                            it.copy(
                                titulo = titulo,
                                descripcion = descripcion,
                                recordatorio = recordatorio,
                                estado = estado
                            )
                        } else {
                            it
                        }
                    }
                    _taskOperationState.value = TaskOperationState.Success
                } else {
                    _taskOperationState.value = TaskOperationState.Error("Failed to update task.")
                }
            } catch (e: Exception) {
                _taskOperationState.value = TaskOperationState.Error("Error updating task: ${e.message}")
                Log.e("TaskViewModel", "Exception while updating task", e)
            }
        }
    }

    /**
     * Resetea el estado de la operacion de creacion/actualizacion. Se llama desde la UI
     * despues de mostrar un mensaje de exito o error.
     */
    fun resetTaskOperationState() {
        _taskOperationState.value = TaskOperationState.Idle
    }

    /**
     * Limpia el mensaje de error para que no se muestre repetidamente.
     */
    fun clearError() {
        _error.value = null
    }
}

/**
 * Representa los estados de la operacion de cierre de sesion.
 */
sealed class LogoutState {
    object Idle : LogoutState()
    object Success : LogoutState()
}

/**
 * Representa los estados para las operaciones de crear o actualizar una tarea.
 */
sealed class TaskOperationState {
    object Idle : TaskOperationState()
    object Loading : TaskOperationState()
    object Success : TaskOperationState()
    data class Error(val message: String) : TaskOperationState()
}
