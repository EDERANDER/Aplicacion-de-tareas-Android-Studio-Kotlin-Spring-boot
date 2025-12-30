package com.unap.aplicaciontareasfinal.network

import com.unap.aplicaciontareasfinal.data.IaRequest
import com.unap.aplicaciontareasfinal.data.IaResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Esta clase actua como la capa de red para interactuar con el endpoint de la IA.
 * Es responsable de configurar el cliente HTTP y realizar las llamadas a la API.
 */
class IaService {
    // `HttpClient` es el componente principal de Ktor para realizar peticiones de red.
    // Se configura aqui para ser reutilizado en todas las funciones del servicio.
    private val client = HttpClient(CIO) {
        // `CIO` (Coroutine-based I/O) es un motor para el cliente Ktor que es completamente asincrono
        // y esta implementado usando corrutinas de Kotlin.

        // `install(ContentNegotiation)` instala un plugin que automaticamente convierte
        // los objetos de datos de Kotlin (data classes) a JSON y viceversa.
        install(ContentNegotiation) {
            json(Json {
                // `ignoreUnknownKeys = true` evita que la app falle si la respuesta JSON del servidor
                // contiene campos que no existen en nuestra data class.
                ignoreUnknownKeys = true
                // `isLenient = true` permite que el analizador de JSON sea mas flexible con el formato.
                isLenient = true
            })
        }
    }

    private val baseUrl = "https://aplicacion-de-tareas-spring-boot.onrender.com/api"

    /**
     * Una `suspend fun` (funcion de suspension) es una funcion que puede ser pausada y reanudada
     * en otro momento. Son la base de las corrutinas en Kotlin y se usan para manejar
     * operaciones largas (como una llamada de red) sin bloquear el hilo principal de la UI.
     *
     * Esta funcion envia una pregunta a la IA y devuelve su respuesta.
     *
     * @param idUsuario El ID del usuario que realiza la pregunta.
     * @param texto La pregunta o prompt para la IA.
     * @return Un `String` con la respuesta de la IA o un mensaje de error.
     */
    suspend fun getIaResponse(idUsuario: Int, texto: String): String {
        // El bloque `try-catch` es esencial para manejar errores de red (ej. sin conexion)
        // o problemas con el servidor.
        return try {
            val response: HttpResponse = client.get("$baseUrl/ia/$idUsuario") {
                contentType(ContentType.Application.Json)
                setBody(IaRequest(texto = texto))
            }
            if (response.status == HttpStatusCode.OK) {
                // Si la respuesta es exitosa (codigo 200 OK), Ktor deserializa el cuerpo
                // de la respuesta a un objeto `IaResponse`.
                val iaResponse = response.body<IaResponse>()
                iaResponse.respuesta
            } else {
                "Error: ${response.status.value} ${response.status.description}"
            }
        } catch (e: Exception) {
            // Si ocurre una excepcion durante la llamada de red, se captura y se devuelve un mensaje de error.
            "Error: No se pudo obtener una respuesta. ${e.message}"
        }
    }
}
