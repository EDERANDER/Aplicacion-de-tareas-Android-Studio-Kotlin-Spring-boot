package com.unap.aplicaciontareasfinal.network

import android.util.Log
import com.unap.aplicaciontareasfinal.data.LoginRequest
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

class UserService {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

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
}
