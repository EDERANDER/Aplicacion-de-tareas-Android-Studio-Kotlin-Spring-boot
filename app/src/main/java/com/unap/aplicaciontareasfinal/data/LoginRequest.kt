package com.unap.aplicaciontareasfinal.data

import kotlinx.serialization.Serializable

/**
 * Representa la solicitud de inicio de sesion enviada a la API.
 *
 * @property email El correo electronico del usuario.
 * @property contraseña La contraseña del usuario.
 */
@Serializable
data class LoginRequest(
    val email: String,
    val contraseña: String
)
