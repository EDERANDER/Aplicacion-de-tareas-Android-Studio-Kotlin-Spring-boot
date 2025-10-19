package com.unap.aplicaciontareasfinal.ui.theme.perfil

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen() {
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showDeleteTasksDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Perfil de Usuario", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Información de la Cuenta", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Juan Pérez", fontWeight = FontWeight.Bold)
                Text("Usuario desde 2021", color = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text("Configuración", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Recibir notificaciones")
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { showDeleteAccountDialog = true }) {
            Text("Eliminar Cuenta", color = Color.Red)
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { showDeleteTasksDialog = true }) {
            Text("Eliminar Todas Las Tareas", color = Color.Red)
        }
    }

    if (showDeleteAccountDialog) {
        DeleteConfirmationDialog(
            title = "Eliminar Cuenta",
            text = "¿Estás seguro de que deseas eliminar tu cuenta? Esta acción es irreversible y perderás todos los datos relacionados.",
            onConfirm = {
                // Lógica para eliminar cuenta
                showDeleteAccountDialog = false
            },
            onDismiss = { showDeleteAccountDialog = false }
        )
    }

    if (showDeleteTasksDialog) {
        DeleteConfirmationDialog(
            title = "Eliminar Tareas",
            text = "¿Estás seguro de que deseas eliminar tus tareas? Esta acción es irreversible y perderás todos los datos relacionados.",
            onConfirm = {
                // Lógica para eliminar tareas
                showDeleteTasksDialog = false
            },
            onDismiss = { showDeleteTasksDialog = false }
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, color = Color.Red, fontWeight = FontWeight.Bold) },
        text = { Text(text = text) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
