package com.unap.aplicaciontareasfinal.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representa una tarea individual de un usuario.
 *
 * @property id El identificador unico de la tarea.
 * @property titulo El titulo de la tarea.
 * @property descripcion Una descripcion detallada de la tarea.
 * @property recordatorio La fecha y hora del recordatorio. Puede ser nulo.
 * @property estado Indica si la tarea esta completada (true) o pendiente (false).
 * @property notificado Indica si se ha enviado una notificacion para la tarea. Puede ser nulo.
 */
@Serializable
data class Task(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val recordatorio: String?, // Hacerlo nullable por si acaso
    @SerialName("estadoTarea")
    val estado: Boolean,
    val notificado: Boolean? // Hacerlo nullable por si acaso
)
