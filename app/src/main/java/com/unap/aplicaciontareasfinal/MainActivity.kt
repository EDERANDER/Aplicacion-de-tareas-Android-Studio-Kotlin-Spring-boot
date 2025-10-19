package com.unap.aplicaciontareasfinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.unap.aplicaciontareasfinal.ui.theme.AplicacionTareasFinalTheme
import com.unap.aplicaciontareasfinal.ui.theme.crud.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTareasTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    // Pantalla de login
                    composable("login") {
                        LoginScreen(
                            onLoginClick = {
                                navController.navigate("main")
                            },
                            onRegisterClick = {
                                navController.navigate("register")
                            }
                        )
                    }
                    composable("register") {
                        RegisterScreen(navController)
                    }

                    // Pantalla principal (después de iniciar sesión)
                    composable("main") {
                        MainScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Bienvenido al asistente virtual universitario")
    }
}

