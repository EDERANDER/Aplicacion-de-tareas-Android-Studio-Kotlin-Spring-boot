package com.unap.aplicaciontareasfinal.ui.theme.inicioSesion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unap.aplicaciontareasfinal.ui.theme.AplicacionTareasFinalTheme
import com.unap.aplicaciontareasfinal.ui.theme.crud.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplicacionTareasFinalTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "main"
                ) {
                    // Pantalla principal (después de iniciar sesión)
                    composable("main") {
                        AppNavigation()
                    }
                }
            }
        }
    }
}

