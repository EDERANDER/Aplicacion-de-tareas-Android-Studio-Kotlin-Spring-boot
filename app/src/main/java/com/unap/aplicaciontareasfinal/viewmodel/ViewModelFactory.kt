package com.unap.aplicaciontareasfinal.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.unap.aplicaciontareasfinal.datastore.UserDataStore
import com.unap.aplicaciontareasfinal.network.TaskService
import com.unap.aplicaciontareasfinal.network.UserService

import com.unap.aplicaciontareasfinal.network.IaService

class ViewModelFactory(
    private val userDataStore: UserDataStore,
    private val userService: UserService,
    private val taskService: TaskService,
    private val iaService: IaService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userService, userDataStore) as T
        }
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(userDataStore, taskService) as T
        }
        if (modelClass.isAssignableFrom(IaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IaViewModel(userDataStore, iaService) as T
        }
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(userDataStore, userService, taskService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val instance = ViewModelFactory(
                    userDataStore = UserDataStore(context.applicationContext),
                    userService = UserService(),
                    taskService = TaskService(),
                    iaService = IaService()
                )
                INSTANCE = instance
                instance
            }
        }
    }
}
