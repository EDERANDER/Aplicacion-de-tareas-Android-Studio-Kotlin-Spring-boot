package com.unap.aplicaciontareasfinal.data

import kotlinx.serialization.Serializable

/**
 * Representa la respuesta de la API al solicitar los datos de un usuario.
 * Incluye los datos del usuario y una lista opcional de sus tareas.
 *
 * @property id El identificador unico del usuario.
 * @property nombre El nombre completo del usuario.
 * @property email El correo electronico del usuario.
 * @property numeroWhatsapp El numero de WhatsApp del usuario.
 * @property date La fecha de creacion de la cuenta del usuario.
 * @property tareas Una lista de las tareas asociadas al usuario. Puede ser nula.
 */
@Serializable
data class UserResponse(
    val id: Int,
    val nombre: String,
    val email: String,
    val numeroWhatsapp: String,
    val date: String,
    val tareas: List<Tarea>? = null
)

/**
 * Representa una tarea dentro de la respuesta del usuario.
 * Nota: Esta clase es similar a 'Task.kt' pero se usa especificamente
 * en el contexto de la respuesta del endpoint de usuario.
 *
 * @property id El identificador unico de la tarea.
 * @property titulo El titulo de la tarea.
 * @property descripcion Una descripcion detallada de la tarea.
 * @property recordatorio La fecha y hora del recordatorio.
 * @property estadoTarea Indica si la tarea esta completada (true) o pendiente (false).
 * @property notificado Indica si se ha enviado una notificacion para la tarea.
 */
@Serializable
data class Tarea(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val recordatorio: String,
    val estadoTarea: Boolean,
    val notificado: Boolean
)
