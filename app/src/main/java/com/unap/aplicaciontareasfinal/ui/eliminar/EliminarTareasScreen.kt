package com.unap.aplicaciontareasfinal.ui.eliminar


import androidx.compose.runtime.Composable

/**
 * Un Composable es una funcion especial en Jetpack Compose que describe una parte de la interfaz de usuario.
 * Compose se encarga de llamar a estas funciones para "dibujar" lo que se ve en la pantalla.
 * La anotacion @Composable le dice al compilador de Kotlin que esta funcion es para la UI.
 */

/**
 * Muestra una pantalla de confirmacion especifica para eliminar todas las tareas de un usuario.
 * Esta funcion reutiliza el componente generico `EliminarScreen` con un texto predefinido.
 *
 * @param onConfirmar Funcion que se llamara cuando el usuario confirme la eliminacion de las tareas.
 * @param onCancelar Funcion que se llamara cuando el usuario cancele la operacion.
 */
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
