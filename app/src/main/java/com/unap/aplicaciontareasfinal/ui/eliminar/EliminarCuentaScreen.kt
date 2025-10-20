package com.unap.aplicaciontareasfinal.ui.eliminar

import androidx.compose.runtime.Composable

@Composable
fun EliminarCuentaScreen(
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    EliminarScreen(
        titulo = "Eliminar Cuenta",
        mensaje = "¿Estás seguro de que deseas eliminar tu cuenta? Esta acción es irreversible y perderás todos los datos relacionados.",
        onConfirmar = onConfirmar,
        onCancelar = onCancelar
    )
}
