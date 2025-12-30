package com.unap.aplicaciontareasfinal.network

import android.util.Log
import com.unap.aplicaciontareasfinal.data.LoginRequest
import com.unap.aplicaciontareasfinal.data.RegisterRequest
import com.unap.aplicaciontareasfinal.data.UserResponse
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
 * Esta clase actua como la capa de red para todas las operaciones relacionadas con el usuario,
 * como el inicio de sesion, el registro y la eliminacion de cuentas.
 */
class UserService {

    // Se configura un cliente Ktor `HttpClient` para realizar las peticiones de red.
    // El plugin `ContentNegotiation` se usa para manejar la conversion de objetos a/desde JSON.
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    /**
     * Envia una peticion al servidor para autenticar a un usuario.
     * Es una `suspend fun` porque realiza una operacion de red que puede tardar.
     *
     * @param loginRequest Un objeto que contiene el email y la contrasena del usuario.
     * @return Un objeto `UserResponse` si el login es exitoso, o `null` si falla.
     */
    suspend fun login(loginRequest: LoginRequest): UserResponse? {
        return try {
            val response: HttpResponse = client.post("https://aplicacion-de-tareas-spring-boot.onrender.com/api/usuarios/login") {
                contentType(ContentType.Application.Json)
                setBody(loginRequest)
            }
            Log.d("UserService", "Response status: ${response.status}")
            val responseBody = response.bodyAsText()
            Log.d("UserService", "Response body: $responseBody")

            if (response.status == HttpStatusCode.OK) {
                // Si la respuesta es 200 OK, se deserializa el cuerpo de la respuesta a un objeto `UserResponse`.
                Json { ignoreUnknownKeys = true }.decodeFromString<UserResponse>(responseBody)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("UserService", "Error during login: ${e.message}")
            null
        }
    }

    /**
     * Envia una peticion al servidor para registrar un nuevo usuario.
     *
     * @param registerRequest Un objeto con todos los datos necesarios para el registro.
     * @return Un objeto `UserResponse` si el registro es exitoso, o `null` si falla.
     */
    suspend fun registerUser(registerRequest: RegisterRequest): UserResponse? {
        return try {
            val response: HttpResponse = client.post("https://aplicacion-de-tareas-spring-boot.onrender.com/api/usuarios/crearUsuario") {
                contentType(ContentType.Application.Json)
                setBody(registerRequest)
            }
            Log.d("UserService", "Register Response status: ${response.status}")
            val responseBody = response.bodyAsText()
            Log.d("UserService", "Register Response body: $responseBody")

            if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK) {
                Json { ignoreUnknownKeys = true }.decodeFromString<UserResponse>(responseBody)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("UserService", "Error during registration: ${e.message}")
            null
        }
    }

    /**
     * Envia una peticion al servidor para eliminar la cuenta de un usuario.
     *
     * @param userId El ID del usuario a eliminar.
     * @return `true` si la eliminacion fue exitosa, `false` en caso contrario.
     */
    suspend fun deleteUser(userId: Int): Boolean {
        return try {
            val response: HttpResponse = client.delete("https://aplicacion-de-tareas-spring-boot.onrender.com/api/usuarios/eliminarUsuario/$userId") {
                contentType(ContentType.Application.Json)
            }
            Log.d("UserService", "Delete Response status: ${response.status}")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("UserService", "Error during user deletion: ${e.message}")
            false
        }
    }
}
