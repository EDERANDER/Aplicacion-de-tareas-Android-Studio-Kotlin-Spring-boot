package com.unap.aplicaciontareasfinal

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F8FF)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Crear Cuenta",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3A2B8A),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Regístrate para comenzar a usar Worki Work",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmarContrasena,
                onValueChange = { confirmarContrasena = it },
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    when {
                        nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty() -> {
                            Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                        }
                        contrasena != confirmarContrasena -> {
                            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(context, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show()
                            navController.navigate("login")
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Registrarse", color = Color.White, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text(
                    text = "¿Ya tienes cuenta? Inicia sesión",
                    color = Color(0xFF3A2B8A),
                    fontSize = 16.sp
                )
            }
        }
    }
}
