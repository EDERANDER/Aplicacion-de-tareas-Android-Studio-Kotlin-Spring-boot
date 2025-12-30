package com.unap.aplicaciontareasfinal.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representa la solicitud para actualizar una tarea existente.
 * Se envia a la API para modificar los detalles de una tarea especifica.
 *
 * @property titulo El nuevo titulo de la tarea.
 * @property descripcion La nueva descripcion de la tarea.
 * @property recordatorio La nueva fecha y hora del recordatorio. Puede ser nulo.
 * @property estado El nuevo estado de la tarea (completada o pendiente).
 */
@Serializable
data class TaskUpdateRequest(
    val titulo: String,
    val descripcion: String,
    val recordatorio: String?,
    @SerialName("estadoTarea")
    val estado: Boolean
)
