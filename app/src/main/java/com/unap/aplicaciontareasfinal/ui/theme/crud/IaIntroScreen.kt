package com.unap.aplicaciontareasfinal.ui.theme.crud

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun IaIntroScreen(onStartChat: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Asistente de Tareas")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Estamos aquí para ayudarte. Empieza una conversación haciendo clic en el botón de abajo.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onStartChat() }) {
            Text("Iniciar Chat")
        }
    }
}
