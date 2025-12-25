package com.unap.aplicaciontareasfinal.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    @SerialName("estadoTarea")
    val estado: Boolean
)
