package com.unap.aplicaciontareasfinal.ui.eliminar


import androidx.compose.runtime.Composable

@Composable
fun EliminarTareasScreen(
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    EliminarScreen(
        titulo = "Eliminar Tareas",
        mensaje = "¿Estás seguro de que deseas eliminar tus tareas? Esta acción es irreversible y perderás todos los datos relacionados.",
        onConfirmar = onConfirmar,
        onCancelar = onCancelar
    )
}
