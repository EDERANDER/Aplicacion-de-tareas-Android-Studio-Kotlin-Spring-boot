package com.unap.aplicaciontareasfinal.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representa la solicitud para crear una nueva tarea.
 * Se envia a la API para registrar una tarea para un usuario especifico.
 *
 * @property titulo El titulo de la nueva tarea.
 * @property descripcion La descripcion de la nueva tarea.
 * @property recordatorio La fecha y hora del recordatorio para la nueva tarea.
 * @property estado El estado inicial de la tarea (completada o pendiente).
 */
@Serializable
data class TaskCreateRequest(
    val titulo: String,
    val descripcion: String,
    val recordatorio: String,
    @SerialName("estadoTarea")
    val estado: Boolean
)
