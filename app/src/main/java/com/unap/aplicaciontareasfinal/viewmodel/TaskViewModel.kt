package com.unap.aplicaciontareasfinal.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unap.aplicaciontareasfinal.data.Task
import com.unap.aplicaciontareasfinal.datastore.UserDataStore
import com.unap.aplicaciontareasfinal.network.TaskService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TaskViewModel(
    private val userDataStore: UserDataStore,
    private val taskService: TaskService
) : ViewModel() {

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun logout() {
        viewModelScope.launch {
            userDataStore.clearData()
            _logoutState.value = LogoutState.Success
        }
    }

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

    fun updateTaskStatus(task: Task, newStatus: Boolean) {
        viewModelScope.launch {
            try {
                val userId = userDataStore.getUser.first()?.id
                if (userId == null) {
                    _error.value = "User ID not found."
                    return@launch
                }

                val taskUpdateRequest = com.unap.aplicaciontareasfinal.data.TaskUpdateRequest(
                    titulo = task.titulo,
                    descripcion = task.descripcion,
                    recordatorio = task.recordatorio,
                    estado = newStatus
                )

                val success = taskService.updateTask(userId, task.id, taskUpdateRequest)

                if (success) {
                    // Actualizar la lista local
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
                    // Actualizar la lista local
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

    fun clearError() {
        _error.value = null
    }
}

sealed class LogoutState {
    object Idle : LogoutState()
    object Success : LogoutState()
}

