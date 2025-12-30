package com.unap.aplicaciontareasfinal.data

import kotlinx.serialization.Serializable

/**
 * Representa el modelo de datos de un usuario en la aplicacion.
 *
 * @property id El identificador unico del usuario.
 * @property nombre El nombre completo del usuario.
 * @property email El correo electronico del usuario.
 * @property numeroWhatsapp El numero de WhatsApp del usuario.
 * @property date La fecha de creacion de la cuenta del usuario.
 */
@Serializable
data class User(
    val id: Int,
    val nombre: String,
    val email: String,
    val numeroWhatsapp: String,
    val date: String
)
