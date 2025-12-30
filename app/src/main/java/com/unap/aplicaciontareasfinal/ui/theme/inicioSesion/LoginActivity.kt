package com.unap.aplicaciontareasfinal.ui.theme.inicioSesion

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unap.aplicaciontareasfinal.R
import com.unap.aplicaciontareasfinal.ui.theme.AplicacionTareasFinalTheme
import com.unap.aplicaciontareasfinal.viewmodel.LoginState
import com.unap.aplicaciontareasfinal.viewmodel.LoginViewModel
import com.unap.aplicaciontareasfinal.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

/**
 * Actividad que maneja la pantalla y la logica para el inicio de sesion de un usuario.
 */
class LoginActivity : ComponentActivity() {

    // `by viewModels` es un delegado de propiedad de Kotlin que proporciona una instancia de ViewModel.
    // El ViewModel sobrevive a cambios de configuracion como la rotacion de pantalla.
    // Se le pasa la factoria singleton `ViewModelFactory` para que sepa como crear el LoginViewModel
    // con sus dependencias (UserService, UserDataStore).
    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AplicacionTareasFinalTheme {
                LoginScreen(
                    loginViewModel = viewModel,
                    onRegisterClick = {
                        // Navega a la pantalla de registro cuando el usuario pulsa el enlace.
                        startActivity(Intent(this, RegisterActivity::class.java))
                    }
                )
            }
        }
    }
}

/**
 * La anotacion @OptIn se usa para habilitar APIs experimentales.
 */
@OptIn(ExperimentalMaterial3Api::class)
/**
 * El Composable que define la interfaz de usuario para la pantalla de inicio de sesion.
 *
 * @param loginViewModel El ViewModel que maneja la logica de inicio de sesion.
 * @param onRegisterClick Funcion lambda que se ejecuta cuando el usuario pulsa el enlace para registrarse.
 */
@Composable
fun LoginScreen(loginViewModel: LoginViewModel, onRegisterClick: () -> Unit) {
    // Estados para los campos de texto y la visibilidad de la contrasena.
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // `collectAsState` observa el `loginState` del ViewModel. La UI se recompone cuando cambia.
    val loginState by loginViewModel.loginState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // `LaunchedEffect` se ejecuta cuando `loginState` cambia.
    // Se usa para reaccionar al resultado del intento de inicio de sesion.
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                // Si el login es exitoso, navega a la actividad principal de la aplicacion.
                context.startActivity(Intent(context, MainActivity::class.java))
                // Se finaliza la actividad actual para que el usuario no pueda volver atras.
                (context as? ComponentActivity)?.finish()
            }
            is LoginState.Error -> {
                // Si hay un error, se muestra en un Snackbar.
                scope.launch {
                    snackbarHostState.showSnackbar(
                        (loginState as LoginState.Error).message
                    )
                }
            }
            else -> {} // No hacer nada en otros estados (Idle, Loading).
        }
    }

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
                            Color.Black.copy(alpha = 0.4f),
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
            // Icono / Logo
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Logo",
                tint = Color.White,
                modifier = Modifier
                    .size(80.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), shape = RoundedCornerShape(50))
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Bienvenido",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Inicia sesión en tu cuenta",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.9f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledContainerColor = Color.White.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff

                    val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                singleLine = true,
                // `visualTransformation` se usa para cambiar la apariencia del texto.
                // Se alterna entre `PasswordVisualTransformation` (oculta el texto) y `VisualTransformation.None` (lo muestra).
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.9f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledContainerColor = Color.White.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Inicio de Sesión
            Button(
                onClick = { loginViewModel.login(email, password) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                if (loginState == LoginState.Loading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(
                        text = "Iniciar sesión",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Enlace de Registro
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "¿No tienes cuenta? ",
                    color = Color.White
                )
                Text(
                    text = "Regístrate aquí",
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onRegisterClick() }
                )
            }
        }
        // `SnackbarHost` es el componente que se encarga de mostrar los mensajes de `snackbarHostState`.
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
