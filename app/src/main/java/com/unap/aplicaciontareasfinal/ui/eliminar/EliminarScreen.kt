package com.unap.aplicaciontareasfinal.ui.eliminar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Un Composable es una funcion especial en Jetpack Compose que describe una parte de la interfaz de usuario.
 * Compose se encarga de llamar a estas funciones para "dibujar" lo que se ve en la pantalla.
 * La anotacion @Composable le dice al compilador de Kotlin que esta funcion es para la UI.
 */

/**
 * Muestra una pantalla generica de confirmacion para acciones de eliminacion.
 * Este componente es reutilizable y se puede configurar con un titulo y un mensaje especificos.
 *
 * @param titulo El texto que se muestra como encabezado de la pantalla.
 * @param mensaje El texto descriptivo que explica la accion que se va a realizar.
 * @param onConfirmar Una funcion lambda que se ejecuta cuando el usuario presiona el boton "Confirmar".
 *                    El tipo `() -> Unit` significa que es una funcion que no recibe parametros y no devuelve nada.
 * @param onCancelar Una funcion lambda que se ejecuta cuando el usuario presiona el boton "Cancelar".
 */
@Composable
fun EliminarScreen(
    titulo: String,
    mensaje: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = titulo,
            color = Color(0xFF9B0000),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.Start)
        )

        Text(
            text = mensaje,
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 40.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onConfirmar,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A148C))
            ) {
                Text("Confirmar")
            }

            OutlinedButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    }
}
