package com.unap.aplicaciontareasfinal.network

import android.util.Log
import com.unap.aplicaciontareasfinal.data.Task
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class TaskService {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true // Permite que el JSON sea un poco más flexible si es necesario
            })
        }
    }

    suspend fun getTasksForUser(userId: Int): List<Task> {
        return try {
            val response: HttpResponse = client.get("https://aplicacion-de-tareas-spring-boot.onrender.com/api/tareas/listaTareas/$userId") {
                contentType(ContentType.Application.Json)
            }
            Log.d("TaskService", "Response status for tasks: ${response.status}")
            val responseBody = response.bodyAsText()
            Log.d("TaskService", "Response body for tasks: $responseBody")

            if (response.status == HttpStatusCode.OK) {
                Json { ignoreUnknownKeys = true; isLenient = true }.decodeFromString<List<Task>>(responseBody)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TaskService", "Error during fetching tasks: ${e.message}")
            emptyList()
        }
    }
}
