package com.unap.aplicaciontareasfinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unap.aplicaciontareasfinal.data.RegisterRequest
import com.unap.aplicaciontareasfinal.network.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Un `ViewModel` es un componente de la arquitectura de Android que esta disenado para almacenar y gestionar
 * datos relacionados con la UI de forma consciente del ciclo de vida. Sobrevive a cambios de configuracion
 * como rotaciones de pantalla, evitando la perdida de datos.
 *
 * Este ViewModel maneja la logica y el estado de la pantalla de registro.
 *
 * @param userService La dependencia para realizar la llamada de red para el registro.
 */
class RegisterViewModel(
    private val userService: UserService
) : ViewModel() {

    // `MutableStateFlow` es un flujo de datos observable que almacena el estado actual.
    // Es "mutable", lo que significa que su valor se puede cambiar desde dentro del ViewModel.
    // Se mantiene privado (`_registerState`) para asegurar que solo el ViewModel lo modifique.
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    // Se expone un `StateFlow` inmutable a la UI. La UI puede observar este flujo
    // para reaccionar a los cambios de estado, pero no puede modificarlo directamente.
    val registerState: StateFlow<RegisterState> = _registerState

    /**
     * Inicia el proceso de registro de un nuevo usuario.
     *
     * @param nombre El nombre del usuario.
     * @param email El correo electronico del usuario.
     * @param numeroWhatsapp El numero de WhatsApp del usuario.
     * @param contraseña La contrasena elegida por el usuario.
     */
    fun register(nombre: String, email: String, numeroWhatsapp: String, contraseña: String) {
        // `viewModelScope` es un ambito de corrutinas integrado en cada ViewModel.
        // Cualquier corrutina lanzada en este ambito se cancelara automaticamente si el ViewModel
        // se destruye, lo que ayuda a prevenir fugas de memoria y trabajo innecesario.
        viewModelScope.launch {
            // Se actualiza el estado a `Loading` para que la UI pueda mostrar un indicador de carga.
            _registerState.value = RegisterState.Loading
            val response = userService.registerUser(RegisterRequest(nombre, email, numeroWhatsapp, contraseña))
            if (response != null) {
                // Si la respuesta de la API no es nula, el registro fue exitoso.
                _registerState.value = RegisterState.Success
            } else {
                // Si la respuesta es nula, ocurrio un error.
                _registerState.value = RegisterState.Error("Registration failed")
            }
        }
    }
}

/**
 * Una `sealed class` (clase sellada) se usa para representar un conjunto finito de estados.
 * Es ideal para modelar el estado de la UI porque el compilador puede verificar que se manejen
 * todos los casos posibles en un `when`.
 *
 * En este caso, representa los posibles estados de la operacion de registro.
 */
sealed class RegisterState {
    // El estado inicial, no ha ocurrido nada.
    object Idle : RegisterState()
    // La operacion esta en curso.
    object Loading : RegisterState()
    // La operacion termino con exito.
    object Success : RegisterState()
    // La operacion fallo. Contiene un mensaje de error.
    data class Error(val message: String) : RegisterState()
}
