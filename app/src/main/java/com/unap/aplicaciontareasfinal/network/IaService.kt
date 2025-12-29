package com.unap.aplicaciontareasfinal.network

import com.unap.aplicaciontareasfinal.data.IaRequest
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class IaService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private val baseUrl = "https://aplicacion-de-tareas-spring-boot.onrender.com/api"

    suspend fun getIaResponse(idUsuario: Int, texto: String): String {
        return try {
            val response: HttpResponse = client.get("$baseUrl/ia/$idUsuario") {
                contentType(ContentType.Application.Json)
                setBody(IaRequest(texto = texto))
            }
            if (response.status == HttpStatusCode.OK) {
                response.bodyAsText()
            } else {
                "Error: ${response.status.value} ${response.status.description}"
            }
        } catch (e: Exception) {
            // Manejo de excepciones de red o de otro tipo.
            "Error: No se pudo obtener una respuesta. ${e.message}"
        }
    }
}
