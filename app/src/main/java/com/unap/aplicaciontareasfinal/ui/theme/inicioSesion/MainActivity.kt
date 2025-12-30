package com.unap.aplicaciontareasfinal.ui.theme.inicioSesion

import AppNavigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unap.aplicaciontareasfinal.ui.theme.AplicacionTareasFinalTheme
import com.unap.aplicaciontareasfinal.viewmodel.TaskViewModel
import com.unap.aplicaciontareasfinal.viewmodel.ViewModelFactory

/**
 * `MainActivity` es la actividad principal de la aplicacion despues de que el usuario ha iniciado sesion.
 * Sirve como el contenedor para todos los Composables que forman la experiencia principal del usuario
 * (lista de tareas, perfil, etc.).
 */
class MainActivity : ComponentActivity() {

    // Obtiene la instancia del ViewModel principal de la aplicacion, `TaskViewModel`,
    // usando la factoria singleton. Este ViewModel sera compartido por todos los Composables
    // dentro de esta actividad.
    private val viewModel: TaskViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // `enableEdgeToEdge` permite que la aplicacion dibuje su contenido de borde a borde,
        // utilizando el area de las barras de sistema (estado y navegacion).
        enableEdgeToEdge()
        setContent {
            AplicacionTareasFinalTheme {
                // `rememberNavController` crea y recuerda un `NavController`, que es el componente
                // central para gestionar la navegacion entre Composables.
                val navController = rememberNavController()

                // `NavHost` es un Composable que define un grafo de navegacion.
                // Asocia las "rutas" (strings) con los Composables que se deben mostrar.
                NavHost(
                    navController = navController,
                    // `startDestination` define la primera pantalla que se muestra.
                    startDestination = "main"
                ) {
                    // `composable(route)` define una pantalla en el grafo de navegacion.
                    composable("main") {
                        // `AppNavigation` es el Composable que contiene la logica de la barra de navegacion
                        // y decide que pantalla mostrar (lista de tareas, perfil, etc.).
                        AppNavigation(viewModel)
                    }
                }
            }
        }
    }
}
