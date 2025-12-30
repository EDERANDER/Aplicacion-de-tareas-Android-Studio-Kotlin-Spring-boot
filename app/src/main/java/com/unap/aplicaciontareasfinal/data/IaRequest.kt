package com.unap.aplicaciontareasfinal.data

import kotlinx.serialization.Serializable

/**
 * Representa la solicitud enviada a la API de IA.
 *
 * @property texto El texto o prompt enviado por el usuario.
 * Serializable, es una anotacion que prepara para convertir el valor de entrada en json
 */
@Serializable
data class IaRequest(
    val texto: String
)
