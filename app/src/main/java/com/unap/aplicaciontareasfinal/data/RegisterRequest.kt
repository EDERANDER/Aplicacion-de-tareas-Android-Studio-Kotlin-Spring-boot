package com.unap.aplicaciontareasfinal.data

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val nombre: String,
    val email: String,
    val numeroWhatsapp: String,
    val contraseña: String
)
