package com.unap.aplicaciontareasfinal.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.unap.aplicaciontareasfinal.datastore.UserDataStore
import com.unap.aplicaciontareasfinal.network.TaskService
import com.unap.aplicaciontareasfinal.network.UserService

import com.unap.aplicaciontareasfinal.network.IaService

/**
 * Una factoria de ViewModels centralizada para toda la aplicacion.
 * Sigue el patron de diseno Singleton para asegurar que solo exista una instancia de la factoria
 * y, por lo tanto, una sola instancia de cada servicio (`UserService`, `TaskService`, etc.).
 * Esto es crucial para que diferentes partes de la app compartan los mismos datos y estado de red.
 *
 * La interfaz `ViewModelProvider.Factory` obliga a implementar el metodo `create`.
 *
 * @param userDataStore El servicio para acceder a los datos locales del usuario.
 * @param userService El servicio para las operaciones de red relacionadas con el usuario.
 * @param taskService El servicio para las operaciones de red relacionadas con las tareas.
 * @param iaService El servicio para las operaciones de red relacionadas con la IA.
 */
class ViewModelFactory(
    private val userDataStore: UserDataStore,
    private val userService: UserService,
    private val taskService: TaskService,
    private val iaService: IaService
) : ViewModelProvider.Factory {

    /**
     * Este metodo es llamado por el sistema cuando se solicita una instancia de un ViewModel.
     * Comprueba que tipo de ViewModel se necesita y lo crea con las dependencias correctas.
     *
     * @param modelClass La clase del ViewModel que se necesita crear.
     * @return Una instancia del ViewModel solicitado.
     * @throws IllegalArgumentException si la factoria no sabe como crear la clase de ViewModel solicitada.
     */
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

    /**
     * `companion object` en Kotlin es similar a los miembros estaticos de Java.
     * Contiene metodos y propiedades que pertenecen a la clase en si, no a una instancia de ella.
     */
    companion object {
        // `@Volatile` asegura que los cambios en esta variable sean visibles para todos los hilos.
        // Es importante en un patron Singleton para evitar problemas de concurrencia.
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        /**
         * Este es el metodo que implementa el patron Singleton.
         * Devuelve la instancia existente de `ViewModelFactory` o crea una nueva si no existe.
         *
         * El bloque `synchronized(this)` asegura que solo un hilo pueda crear la instancia a la vez,
         * previniendo la creacion de multiples instancias en un entorno multi-hilo.
         *
         * @param context El contexto de la aplicacion, necesario para inicializar `UserDataStore`.
         * @return La unica instancia de `ViewModelFactory`.
         */
        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val instance = ViewModelFactory(
                    // Se usa `applicationContext` para evitar fugas de memoria.
                    // El `UserDataStore` vivira tanto como la aplicacion.
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
