package com.unap.aplicaciontareasfinal.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskCreateRequest(
    val titulo: String,
    val descripcion: String,
    val recordatorio: String,
    @SerialName("estadoTarea")
    val estado: Boolean
)
