package com.unap.aplicaciontareasfinal.data

import kotlinx.serialization.Serializable

/**
 * Representa la respuesta recibida de la API de IA.
 *
 * @property pregunta La pregunta original enviada por el usuario.
 * @property respuesta La respuesta generada por la IA.
 */
@Serializable
data class IaResponse(
    val pregunta: String,
    val respuesta: String
)
