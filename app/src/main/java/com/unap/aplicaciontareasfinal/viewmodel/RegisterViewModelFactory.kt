package com.unap.aplicaciontareasfinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.unap.aplicaciontareasfinal.network.UserService

/**
 * Una `ViewModelProvider.Factory` es una clase que sabe como crear instancias de ViewModels.
 * Es necesaria cuando un ViewModel tiene un constructor con parametros (dependencias),
 * ya que el sistema por si solo no sabria como proporcionar esas dependencias.
 *
 * Esta factoria en especifico es responsable de crear instancias de `RegisterViewModel`.
 *
 * @param userService La dependencia (el servicio de usuario) que necesita `RegisterViewModel`.
 */
class RegisterViewModelFactory(
    private val userService: UserService
) : ViewModelProvider.Factory {

    /**
     * Este metodo es llamado por el sistema cuando se solicita una instancia de un ViewModel.
     *
     * @param modelClass La clase del ViewModel que se necesita crear.
     * @return Una instancia del ViewModel solicitado.
     * @throws IllegalArgumentException si la factoria no sabe como crear la clase de ViewModel solicitada.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // `isAssignableFrom` comprueba si `modelClass` es `RegisterViewModel` o una subclase.
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            // Si es el ViewModel que esta factoria sabe crear, lo instancia con su dependencia.
            // `@Suppress("UNCHECKED_CAST")` se usa para suprimir una advertencia del compilador,
            // ya que hemos comprobado manualmente que el tipo es correcto.
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(userService) as T
        }
        // Si se solicita un ViewModel diferente, lanza una excepcion porque no sabe como crearlo.
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
