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

    fun clearError() {
        _error.value = null
    }
}

sealed class LogoutState {
    object Idle : LogoutState()
    object Success : LogoutState()
}

