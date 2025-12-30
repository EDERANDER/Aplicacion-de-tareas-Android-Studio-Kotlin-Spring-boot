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

/**
 * Esta clase actua como la capa de red para todas las operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * relacionadas con las tareas.
 */
class TaskService {

    // Se configura un cliente Ktor `HttpClient` para realizar las peticiones de red.
    // El plugin `ContentNegotiation` se usa para manejar la conversion de objetos a/desde JSON.
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true // Permite que el JSON sea un poco más flexible si es necesario
            })
        }
    }

    /**
     * Obtiene la lista de todas las tareas de un usuario especifico.
     *
     * @param userId El ID del usuario cuyas tareas se quieren obtener.
     * @return Una `List<Task>` con las tareas del usuario. Devuelve una lista vacia si ocurre un error.
     */
    suspend fun getTasksForUser(userId: Int): List<Task> {
        return try {
            val response: HttpResponse =
                client.get("https://aplicacion-de-tareas-spring-boot.onrender.com/api/tareas/listaTareas/$userId") {
                    contentType(ContentType.Application.Json)
                }
            Log.d("TaskService", "Response status for tasks: ${response.status}")
            val responseBody = response.bodyAsText()
            Log.d("TaskService", "Response body for tasks: $responseBody")

            if (response.status == HttpStatusCode.OK) {
                Json { ignoreUnknownKeys = true; isLenient = true }.decodeFromString<List<Task>>(
                    responseBody
                )
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TaskService", "Error during fetching tasks: ${e.message}")
            emptyList()
        }
    }

    /**
     * Actualiza una tarea existente en el servidor.
     *
     * @param userId El ID del usuario propietario de la tarea.
     * @param taskId El ID de la tarea a actualizar.
     * @param taskUpdateRequest Un objeto con los nuevos datos de la tarea.
     * @return `true` si la actualizacion fue exitosa, `false` en caso contrario.
     */
    suspend fun updateTask(
        userId: Int,
        taskId: Int,
        taskUpdateRequest: com.unap.aplicaciontareasfinal.data.TaskUpdateRequest
    ): Boolean {
        val url =
            "https://aplicacion-de-tareas-spring-boot.onrender.com/api/tareas/actualizarTarea/$userId/$taskId"
        Log.d("TaskService", "Attempting to update task with URL: $url")
        try {
            val jsonBody = Json.encodeToString(
                com.unap.aplicaciontareasfinal.data.TaskUpdateRequest.serializer(),
                taskUpdateRequest
            )
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

    /**
     * Elimina una tarea especifica del servidor.
     *
     * @param userId El ID del usuario propietario de la tarea.
     * @param taskId El ID de la tarea a eliminar.
     * @return `true` si la eliminacion fue exitosa, `false` en caso contrario.
     */
    suspend fun deleteTask(userId: Int, taskId: Int): Boolean {
        val url =
            "https://aplicacion-de-tareas-spring-boot.onrender.com/api/tareas/eliminarTarea/$userId/$taskId"
        Log.d("TaskService", "Attempting to delete task with URL: $url")
        return try {
            val response: HttpResponse = client.delete(url) {
                contentType(ContentType.Application.Json)
            }
            Log.d("TaskService", "Delete task response status: ${response.status}")
            // Una eliminacion exitosa puede devolver 200 OK o 204 No Content.
            response.status == HttpStatusCode.OK || response.status == HttpStatusCode.NoContent
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TaskService", "Error during task deletion: ${e.message}")
            false
        }
    }

    /**
     * Crea una nueva tarea en el servidor.
     *
     * @param userId El ID del usuario que crea la tarea.
     * @param taskCreateRequest Un objeto con los datos de la nueva tarea.
     * @return Un objeto `Task` con los datos de la tarea creada (incluyendo su nuevo ID), o `null` si falla.
     */
    suspend fun createTask(
        userId: Int,
        taskCreateRequest: com.unap.aplicaciontareasfinal.data.TaskCreateRequest
    ): Task? {
        val url =
            "https://aplicacion-de-tareas-spring-boot.onrender.com/api/tareas/crearTarea/$userId"
        Log.d("TaskService", "Attempting to create task with URL: $url")
        return try {
            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(taskCreateRequest)
            }
            Log.d("TaskService", "Create task response status: ${response.status}")
            if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TaskService", "Error during task creation: ${e.message}")
            null
        }
    }

    /**
     * Elimina todas las tareas de un usuario.
     *
     * @param userId El ID del usuario cuyas tareas seran eliminadas.
     * @return `true` si la operacion fue exitosa, `false` en caso contrario.
     */
    suspend fun deleteAllTasks(userId: Int): Boolean {
        val url = "https://aplicacion-de-tareas-spring-boot.onrender.com/api/tareas/eliminarTodo/$userId"
        Log.d("TaskService", "Attempting to delete all tasks for user with URL: $url")
        return try {
            val response: HttpResponse = client.delete(url) {
                contentType(ContentType.Application.Json)
            }
            Log.d("TaskService", "Delete all tasks response status: ${response.status}")
            response.status == HttpStatusCode.OK || response.status == HttpStatusCode.NoContent
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TaskService", "Error during deleting all tasks: ${e.message}")
            false
        }
    }
}
