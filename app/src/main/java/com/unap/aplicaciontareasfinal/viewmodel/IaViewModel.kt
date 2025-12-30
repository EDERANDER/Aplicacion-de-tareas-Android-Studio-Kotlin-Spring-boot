package com.unap.aplicaciontareasfinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unap.aplicaciontareasfinal.datastore.UserDataStore
import com.unap.aplicaciontareasfinal.network.IaService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Una `data class` en Kotlin es una clase cuyo proposito principal es contener datos.
 * El compilador genera automaticamente metodos utiles como `equals()`, `hashCode()`, `toString()`, etc.
 *
 * Esta clase representa un unico mensaje en el chat.
 *
 * @param text El contenido del mensaje.
 * @param isFromUser Un booleano que indica si el mensaje fue enviado por el usuario (true) o por la IA (false).
 */
data class ChatMessage(
    val text: String,
    val isFromUser: Boolean
)

/**
 * Este ViewModel maneja la logica y el estado de la pantalla de chat con la IA.
 *
 * @param userDataStore Para obtener el ID del usuario logueado.
 * @param iaService Para realizar la llamada de red al servicio de la IA.
 */
class IaViewModel(
    private val userDataStore: UserDataStore,
    private val iaService: IaService
) : ViewModel() {

    // `_messages` almacena la lista de todos los mensajes en la conversacion actual.
    // Es un StateFlow para que la UI pueda observar y redibujarse cuando se anaden mensajes.
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // `_isLoading` controla si se debe mostrar una animacion de carga (cuando la IA esta "pensando").
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // `_prompt` almacena el texto que el usuario esta escribiendo actualmente en el campo de texto.
    private val _prompt = MutableStateFlow("")
    val prompt: StateFlow<String> = _prompt.asStateFlow()

    /**
     * Actualiza el valor del prompt actual. Se llama desde la UI cada vez que el usuario teclea.
     * @param newPrompt El nuevo texto del campo de entrada.
     */
    fun updatePrompt(newPrompt: String) {
        _prompt.value = newPrompt
    }

    /**
     * Envia el prompt actual a la API de IA y actualiza la conversacion con la respuesta.
     */
    fun sendMessage() {
        val currentPrompt = _prompt.value
        // Evita enviar mensajes en blanco.
        if (currentPrompt.isBlank()) return

        viewModelScope.launch {
            // Anade inmediatamente el mensaje del usuario a la lista para una respuesta visual rapida.
            _messages.value = _messages.value + ChatMessage(currentPrompt, true)
            _isLoading.value = true
            // Limpia el campo de texto en la UI inmediatamente despues de enviar.
            _prompt.value = ""

            try {
                val user = userDataStore.getUser.first()
                if (user != null) {
                    val response = iaService.getIaResponse(user.id, currentPrompt)
                    // Anade la respuesta de la IA a la lista de mensajes.
                    _messages.value = _messages.value + ChatMessage(response, false)
                } else {
                    _messages.value = _messages.value + ChatMessage("Error: No se pudo encontrar el usuario.", false)
                }
            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage("Error: ${e.message}", false)
            } finally {
                // Se asegura de que el indicador de carga se oculte siempre, incluso si hay un error.
                _isLoading.value = false
            }
        }
    }

    /**
     * Se llama cuando la pantalla del chat se descarta (el usuario navega a otro lugar).
     * Resetea el estado para evitar comportamientos no deseados la proxima vez que se entre a la pantalla.
     */
    fun onScreenDisposed() {
        _prompt.value = ""
        _isLoading.value = false
    }
}
