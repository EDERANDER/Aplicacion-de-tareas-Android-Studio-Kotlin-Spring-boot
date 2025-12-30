package com.unap.aplicaciontareasfinal.ui.theme.crud

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unap.aplicaciontareasfinal.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * NOTA: Este componente parece ser una version anterior o una pantalla de edicion de tareas no utilizada.
 * La logica de edicion principal de la aplicacion se encuentra actualmente en `AddTaskScreen` cuando se le
 * pasa un parametro `taskToEdit`. Este archivo podria ser obsoleto.
 */

/**
 * La anotacion @OptIn se usa para habilitar APIs que el creador ha marcado como experimentales.
 * En este caso, habilita componentes de Material Design 3.
 */
@OptIn(ExperimentalMaterial3Api::class)
/**
 * Un Composable que representa una pantalla para editar una tarea.
 * Utiliza estados locales para manejar los campos del formulario y muestra dialogos de seleccion de fecha.
 *
 * @param onBack Funcion lambda que se llama para navegar hacia atras.
 */
@Composable
fun EditTaskScreen(onBack: () -> Unit) {

    // `remember` se usa para mantener objetos en memoria a traves de las recomposiciones.
    // `SnackbarHostState` controla la visualizacion de mensajes emergentes (Snackbars).
    val snackbarHostState = remember { SnackbarHostState() }
    // `rememberCoroutineScope` obtiene un ambito de corrutina para lanzar corrutinas desde callbacks (ej. onClick).
    val scope = rememberCoroutineScope()

    val formatter = remember {
        SimpleDateFormat("dd 'de' MMMM yyyy", Locale("es", "PE"))
    }

    /* 🔹 Datos existentes de la tarea */
    // `remember` con `mutableStateOf` crea un estado observable que, al cambiar, recompone la UI.
    // Aqui se usan para almacenar los valores de los campos del formulario, con datos de ejemplo.
    var title by remember {
        mutableStateOf("Desarrollo de plataformas")
    }
    var description by remember {
        mutableStateOf(
            "Esta tarea que dejó el ing. trata sobre el desarrollo de aplicaciones móviles en Android Studio utilizando Kotlin."
        )
    }

    var startMillis by remember { mutableStateOf<Long?>(null) }
    var endMillis by remember { mutableStateOf<Long?>(null) }

    var startDate by remember { mutableStateOf("Inicio") }
    var endDate by remember { mutableStateOf("Final") }

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        /* 🔹 Fondo */
        Image(
            painter = painterResource(id = R.drawable.fondo_agregar_tarea),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Scaffold proporciona la estructura basica de la pantalla.
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Editar Tarea", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Se usa para agrupar visualmente los campos del formulario.
                BubbleCard {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                BubbleCard {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                }

                /* 🔹 Fechas lado a lado */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    DateBubble(
                        modifier = Modifier.weight(1f),
                        title = "Inicio",
                        date = startDate,
                        isSelected = startMillis != null,
                        onClick = { showStartPicker = true }
                    )

                    DateBubble(
                        modifier = Modifier.weight(1f),
                        title = "Final",
                        date = endDate,
                        isSelected = endMillis != null,
                        onClick = { showEndPicker = true }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        // `scope.launch` inicia una corrutina para realizar operaciones asincronas,
                        // como mostrar un Snackbar, sin bloquear el hilo principal de la UI.
                        scope.launch {
                            when {
                                title.isBlank() ->
                                    snackbarHostState.showSnackbar("El título es obligatorio")

                                startMillis == null || endMillis == null ->
                                    snackbarHostState.showSnackbar("Selecciona ambas fechas")

                                startMillis!! > endMillis!! ->
                                    snackbarHostState.showSnackbar("La fecha de inicio no puede ser mayor")

                                else -> {
                                    // 🔹 Aquí luego puedes actualizar en BD / ViewModel
                                    onBack()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Actualizar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    /* 🔹 DatePicker Inicio */
    if (showStartPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        startMillis = it
                        startDate = formatDateSafe(it, formatter)
                    }
                    showStartPicker = false
                }) { Text("Aceptar") }
            }
        ) { DatePicker(state = state) }
    }

    /* 🔹 DatePicker Final */
    if (showEndPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        endMillis = it
                        endDate = formatDateSafe(it, formatter)
                    }
                    showEndPicker = false
                }) { Text("Aceptar") }
            }
        ) { DatePicker(state = state) }
    }
}

/**
 * Funcion de utilidad para formatear de forma segura una fecha a partir de milisegundos.
 * Devuelve "Fecha invalida" si ocurre un error.
 *
 * @param millis Los milisegundos a formatear.
 * @param formatter El `SimpleDateFormat` a utilizar.
 * @return La fecha formateada como String.
 */
fun formatDateSafe(
    millis: Long,
    formatter: SimpleDateFormat
): String {
    return try {
        formatter.format(Date(millis))
    } catch (e: Exception) {
        "Fecha inválida"
    }
}
