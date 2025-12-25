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

    suspend fun updateTask(userId: Int, taskId: Int, taskUpdateRequest: com.unap.aplicaciontareasfinal.data.TaskUpdateRequest): Boolean {
        val url = "https://aplicacion-de-tareas-spring-boot.onrender.com/api/tareas/actualizarTarea/$userId/$taskId"
        Log.d("TaskService", "Attempting to update task with URL: $url")
        try {
            val jsonBody = Json.encodeToString(com.unap.aplicaciontareasfinal.data.TaskUpdateRequest.serializer(), taskUpdateRequest)
            Log.d("TaskService", "Request Body: $jsonBody")

            val response: HttpResponse = client.put(url) {
                contentType(ContentType.Application.Json)
                setBody(taskUpdateRequest)
            }

            val responseBody = response.bodyAsText()
            Log.d("TaskService", "Update task response status: ${response.status}")
            Log.d("TaskService", "Update task response body: $responseBody")

            return response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TaskService", "Error during task update: ${e.message}")
            return false
        }
    }

    suspend fun deleteTask(userId: Int, taskId: Int): Boolean {
        val url = "https://aplicacion-de-tareas-spring-boot.onrender.com/api/tareas/eliminarTarea/$userId/$taskId"
        Log.d("TaskService", "Attempting to delete task with URL: $url")
        return try {
            val response: HttpResponse = client.delete(url) {
                contentType(ContentType.Application.Json)
            }
            Log.d("TaskService", "Delete task response status: ${response.status}")
            response.status == HttpStatusCode.OK || response.status == HttpStatusCode.NoContent
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TaskService", "Error during task deletion: ${e.message}")
            false
        }
    }
}
