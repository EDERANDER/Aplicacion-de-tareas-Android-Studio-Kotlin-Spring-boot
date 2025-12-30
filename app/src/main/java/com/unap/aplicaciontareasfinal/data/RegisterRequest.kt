package com.unap.aplicaciontareasfinal.data

import kotlinx.serialization.Serializable

/**
 * Representa la solicitud de registro de un nuevo usuario enviada a la API.
 *
 * @property nombre El nombre completo del usuario.
 * @property email El correo electronico del usuario.
 * @property numeroWhatsapp El numero de WhatsApp del usuario.
 * @property contraseña La contraseña elegida por el usuario.
 */
@Serializable
data class RegisterRequest(
    val nombre: String,
    val email: String,
    val numeroWhatsapp: String,
    val contraseña: String
)
