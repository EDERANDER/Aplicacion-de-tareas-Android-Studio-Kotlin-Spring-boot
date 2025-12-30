package com.unap.aplicaciontareasfinal.ui.theme.inicioSesion

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import com.unap.aplicaciontareasfinal.datastore.UserDataStore
import com.unap.aplicaciontareasfinal.ui.theme.AplicacionTareasFinalTheme
import kotlinx.coroutines.launch

/**
 * Esta Actividad muestra los terminos y condiciones de la aplicacion.
 * El usuario debe aceptarlos para poder continuar.
 */
class TermsActivity : ComponentActivity() {

    // `by lazy` es un delegado de propiedad de Kotlin. El objeto `UserDataStore` solo se
    // inicializara la primera vez que se acceda a el, no cuando se crea la Actividad.
    // Es una forma de optimizar la inicializacion de objetos pesados.
    private val userDataStore by lazy { UserDataStore(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // `rememberCoroutineScope` obtiene un ambito de corrutina que esta ligado al ciclo de vida
            // del Composable. Se usa para lanzar corrutinas en respuesta a eventos de la UI.
            val scope = rememberCoroutineScope()
            AplicacionTareasFinalTheme {
                TermsScreen(onAccept = {
                    // Se lanza una corrutina para realizar una operacion de guardado en segundo plano
                    // sin bloquear el hilo principal de la UI.
                    scope.launch {
                        // Se guarda en `UserDataStore` que el usuario ya ha visto esta pantalla.
                        // `DataStore` es la solucion moderna de Android para guardar pequeñas cantidades
                        // de datos de forma asincrona, reemplazando a `SharedPreferences`.
                        userDataStore.setHasSeenOnboarding(true)

                        // Navega a la pantalla de Login y cierra esta.
                        startActivity(Intent(this@TermsActivity, LoginActivity::class.java))
                        finish()
                    }
                })
            }
        }
    }
}

/**
 * El Composable que muestra el contenido de la pantalla de terminos y condiciones.
 *
 * @param onAccept Funcion lambda que se ejecuta cuando el usuario pulsa el boton "Aceptar".
 */
@Composable
fun TermsScreen(onAccept: () -> Unit) {
    // `rememberScrollState` crea y recuerda el estado de scroll para un `Column` o `Row`
    // que use el modificador `verticalScroll` o `horizontalScroll`.
    val scrollState = rememberScrollState()

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
                            Color.Black.copy(alpha = 0.6f),
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        // El modificador `verticalScroll` hace que el contenido de la columna sea desplazable
        // si es mas grande que la pantalla. Requiere un `ScrollState`.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = "Términos",
                tint = Color.White,
                modifier = Modifier
                    .size(80.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), shape = RoundedCornerShape(50))
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Términos y Condiciones",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Bienvenido a nuestra aplicación de tareas. " +
                        "Al usar nuestros servicios, estás aceptando los siguientes términos y condiciones:",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Contenido de los términos alineado a la izquierda
            Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                SectionTitle("1. Aceptación de los Términos")
                SectionText("Al registrarte y utilizar esta aplicación, confirmas que has leído y entendido estos términos.")

                SectionTitle("2. Responsabilidades del Usuario")
                SectionText("Eres responsable de mantener la confidencialidad de tu cuenta y de todas las actividades que ocurran bajo tu cuenta.")

                SectionTitle("3. Pago de Servicio")
                SectionText("Al obtener nuestra aplicación te informamos que podemos utilizar tu información para nuestros fines.")
            }

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = onAccept,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Text(
                    text = "Aceptar",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Composable de ayuda para mostrar un titulo de seccion.
 * @param title El texto del titulo.
 */
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = Color.White,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

/**
 * Composable de ayuda para mostrar un parrafo de texto de una seccion.
 * @param text El texto del parrafo.
 */
@Composable
fun SectionText(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        color = Color.White.copy(alpha = 0.8f),
        modifier = Modifier.padding(bottom = 12.dp),
        textAlign = TextAlign.Justify
    )
}
