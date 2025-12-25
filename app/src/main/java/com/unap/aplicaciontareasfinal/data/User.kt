package com.unap.aplicaciontareasfinal.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val nombre: String,
    val email: String,
    val numeroWhatsapp: String,
    val date: String
)
