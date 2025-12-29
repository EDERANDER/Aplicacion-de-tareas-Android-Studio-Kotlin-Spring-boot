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

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean
)

class IaViewModel(
    private val userDataStore: UserDataStore,
    private val iaService: IaService
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(prompt: String) {
        viewModelScope.launch {
            // Add user message to the list
            _messages.value = _messages.value + ChatMessage(prompt, true)
            _isLoading.value = true

            try {
                val user = userDataStore.getUser.first()
                if (user != null) {
                    val response = iaService.getIaResponse(user.id, prompt)
                    // Add IA response to the list
                    _messages.value = _messages.value + ChatMessage(response, false)
                } else {
                    // Handle error: user not found
                    _messages.value = _messages.value + ChatMessage("Error: No se pudo encontrar el usuario.", false)
                }
            } catch (e: Exception) {
                // Handle network or other errors
                _messages.value = _messages.value + ChatMessage("Error: ${e.message}", false)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
