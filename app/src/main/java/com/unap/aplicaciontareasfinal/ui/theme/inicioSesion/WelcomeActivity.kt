package com.unap.aplicaciontareasfinal.ui.theme.inicioSesion

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unap.aplicaciontareasfinal.R
import com.unap.aplicaciontareasfinal.ui.theme.AplicacionTareasFinalTheme

/**
 * Una `Activity` es un componente principal de una aplicacion de Android que representa una unica pantalla
 * con la que un usuario puede interactuar. `ComponentActivity` es la clase base para actividades que usan Jetpack Compose.
 *
 * Esta `WelcomeActivity` es la pantalla de bienvenida que se muestra al iniciar la aplicacion por primera vez.
 */
class WelcomeActivity : ComponentActivity() {
    /**
     * El metodo `onCreate` se llama cuando la actividad se esta iniciando.
     * Es donde se debe realizar la mayor parte de la inicializacion.
     *
     * @param savedInstanceState Si la actividad se esta re-creando despues de haber sido destruida
     *                           (por ejemplo, al rotar la pantalla), este Bundle contiene el estado mas reciente.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // `setContent` es la funcion principal de Compose en una actividad.
        // Define la interfaz de usuario de la actividad utilizando funciones Composable.
        setContent {
            // `AplicacionTareasFinalTheme` es un Composable que define el tema de la aplicacion
            // (colores, tipografias, formas) para todos los Composables hijos.
            AplicacionTareasFinalTheme {
                WelcomeScreen(
                    onStartClick = {
                        // Un `Intent` es un objeto de mensajeria que puedes usar para solicitar una accion
                        // de otro componente de la aplicacion. En este caso, se usa para iniciar una nueva actividad.
                        val intent = Intent(this, TermsActivity::class.java)
                        startActivity(intent)
                        // `finish()` cierra la actividad actual para que el usuario no pueda volver a ella
                        // presionando el boton "atras".
                        finish()
                    }
                )
            }
        }
    }
}

/**
 * Este es el Composable que define la apariencia visual de la pantalla de bienvenida.
 * Es una funcion sin estado propio (stateless) que solo muestra informacion y reacciona a los clics.
 *
 * @param onStartClick Una funcion lambda que se ejecuta cuando el usuario presiona el boton "Comenzar".
 */
@Composable
fun WelcomeScreen(onStartClick: () -> Unit) {
    // `Box` es un contenedor que permite apilar elementos uno encima de otro.
    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo con imagen y gradiente
        Image(
            painter = painterResource(id = R.drawable.fondo_ia),
            contentDescription = "Fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Icono o Logo Central
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Logo",
                tint = Color.White,
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), shape = RoundedCornerShape(50))
                    .padding(20.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Worki Work",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Organiza. Completa. Triunfa.",
                fontSize = 20.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

             Text(
                text = "Tu asistente personal para el éxito académico y profesional.",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                 modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(60.dp))

            Button(
                onClick = onStartClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = ButtonDefaults.buttonElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Comenzar",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
