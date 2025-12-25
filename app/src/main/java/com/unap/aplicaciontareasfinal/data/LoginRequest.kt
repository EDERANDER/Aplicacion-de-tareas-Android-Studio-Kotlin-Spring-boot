package com.unap.aplicaciontareasfinal.data

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val contraseña: String
)
