package com.unap.aplicaciontareasfinal.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unap.aplicaciontareasfinal.data.LoginRequest
import com.unap.aplicaciontareasfinal.data.User
import com.unap.aplicaciontareasfinal.datastore.UserDataStore
import com.unap.aplicaciontareasfinal.network.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Este ViewModel maneja la logica y el estado de la pantalla de inicio de sesion.
 * Se encarga de comunicarse con `UserService` para autenticar al usuario y con
 * `UserDataStore` para guardar los datos del usuario si el inicio de sesion es exitoso.
 *
 * @param userService El servicio para realizar la llamada de red para el login.
 * @param userDataStore El servicio para guardar los datos del usuario localmente.
 */
class LoginViewModel(
    private val userService: UserService,
    private val userDataStore: UserDataStore
) : ViewModel() {

    // `MutableStateFlow` para el estado de la operacion de login. Es privado para que
    // solo este ViewModel pueda modificarlo.
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    // Se expone un `StateFlow` inmutable a la UI para que pueda observar los cambios de estado.
    val loginState: StateFlow<LoginState> = _loginState

    /**
     * Inicia el proceso de inicio de sesion.
     *
     * @param email El correo electronico del usuario.
     * @param contrasenia La contrasena del usuario.
     */
    fun login(email: String, contrasenia: String) {
        // `viewModelScope.launch` inicia una corrutina en el ambito del ViewModel.
        // Se cancelara automaticamente si el ViewModel se destruye.
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val response = userService.login(LoginRequest(email, contrasenia))
            if (response != null) {
                // Si la respuesta es exitosa, se crea un objeto `User` y se guarda
                // en el DataStore para futuras sesiones.
                val user = User(
                    id = response.id,
                    nombre = response.nombre,
                    email = response.email,
                    numeroWhatsapp = response.numeroWhatsapp,
                    date = response.date
                )
                userDataStore.saveUser(user)
                Log.d("LoginViewModel", "User saved with ID: ${user.id}")
                _loginState.value = LoginState.Success
            } else {
                // Si la respuesta es nula, las credenciales son invalidas.
                _loginState.value = LoginState.Error("Invalid credentials")
            }
        }
    }
}

/**
 * Representa los diferentes estados posibles de la pantalla de inicio de sesion.
 * Usar una clase sellada permite un manejo de estados mas seguro y claro.
 */
sealed class LoginState {
    // Estado inicial, la pantalla esta esperando interaccion.
    object Idle : LoginState()
    // El proceso de inicio de sesion esta en curso. La UI puede mostrar una animacion de carga.
    object Loading : LoginState()
    // El inicio de sesion fue exitoso.
    object Success : LoginState()
    // Ocurrio un error durante el inicio de sesion. Contiene un mensaje para mostrar al usuario.
    data class Error(val message: String) : LoginState()
}
