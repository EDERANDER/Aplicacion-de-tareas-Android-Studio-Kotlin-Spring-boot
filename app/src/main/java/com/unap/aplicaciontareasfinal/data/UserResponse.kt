package com.unap.aplicaciontareasfinal.data

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Int,
    val nombre: String,
    val email: String,
    val numeroWhatsapp: String,
    val date: String,
    val tareas: List<Tarea>? = null
)

@Serializable
data class Tarea(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val recordatorio: String,
    val estadoTarea: Boolean,
    val notificado: Boolean
)
