package com.unap.aplicaciontareasfinal.ui.theme.crud

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IaChatScreen() {
    var message by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Asistente de Tareas") })
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it).padding(16.dp)
        ) {
            // Chat messages
            Text("Hola, ¿en qué puedo ayudarte hoy?")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Necesito que me des recomendaciones sobre que tarea hacer primero.")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Claro con mucho gusto la tarea mas cercana que tienes es la de DESARROLLO DE PLATAFORMAS.")
            Spacer(modifier = Modifier.height(8.dp))
            Text("bien muchas gracias.")

            Spacer(modifier = Modifier.weight(1f))

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Escribe tu pregunta o pide una recomendación") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
