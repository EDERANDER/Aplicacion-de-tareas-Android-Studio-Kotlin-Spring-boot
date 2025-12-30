package com.unap.aplicaciontareasfinal.ui.theme.crud

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unap.aplicaciontareasfinal.R
import com.unap.aplicaciontareasfinal.data.Task
import com.unap.aplicaciontareasfinal.viewmodel.TaskOperationState
import com.unap.aplicaciontareasfinal.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * La anotacion @OptIn se usa para habilitar APIs que el creador ha marcado como experimentales.
 * En este caso, habilita componentes de Material Design 3 que aun podrian cambiar en el futuro.
 */
@OptIn(ExperimentalMaterial3Api::class)
/**
 * Este Composable representa la pantalla para agregar una nueva tarea o editar una existente.
 * La logica cambia dependiendo de si `taskToEdit` es nulo (modo creacion) o no (modo edicion).
 *
 * @param taskViewModel El ViewModel que maneja la logica de negocio para crear y actualizar tareas.
 * @param onBack Funcion lambda que se llama para navegar hacia atras una vez que la operacion termina.
 * @param taskToEdit Tarea opcional. Si se proporciona, la pantalla entra en modo de edicion
 *                   y los campos se rellenan con los datos de esta tarea. Si es nulo, es modo creacion.
 */
@Composable
fun AddTaskScreen(
    taskViewModel: TaskViewModel,
    onBack: () -> Unit,
    taskToEdit: Task? = null
) {
    // `LocalContext.current` obtiene el contexto de Android, necesario para mostrar Toasts.
    val context = LocalContext.current
    // `collectAsState` convierte el StateFlow del ViewModel en un State de Compose.
    // La UI se recompone cuando el estado de la operacion (exito, error, etc.) cambia.
    val taskOperationState by taskViewModel.taskOperationState.collectAsState()

    // Se usa `remember` con `mutableStateOf` para cada campo del formulario.
    // Esto crea un estado interno para el Composable que sobrevive a las recomposiciones.
    // Cuando el usuario escribe en un campo, el estado se actualiza y la UI se redibuja.
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    var year by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var month by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var day by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }

    // `LaunchedEffect` se usa para efectos secundarios que deben ocurrir en respuesta a un cambio de estado.
    // Este efecto se ejecuta una vez si `taskToEdit` no es nulo.
    // Se usa para rellenar los campos del formulario con los datos de la tarea a editar.
    LaunchedEffect(taskToEdit) {
        taskToEdit?.let {
            title = it.titulo
            description = it.descripcion
            it.recordatorio?.let { reminder ->
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                try {
                    val date = formatter.parse(reminder)
                    date?.let {
                        val cal = Calendar.getInstance().apply { time = it }
                        year = cal.get(Calendar.YEAR)
                        month = cal.get(Calendar.MONTH)
                        day = cal.get(Calendar.DAY_OF_MONTH)
                        hour = String.format("%02d", cal.get(Calendar.HOUR_OF_DAY))
                        minute = String.format("%02d", cal.get(Calendar.MINUTE))
                    }
                } catch (e: Exception) {
                    println("Error parsing reminder date: $e")
                }
            }
        }
    }

    // Este efecto se ejecuta cada vez que `taskOperationState` cambia.
    // Se usa para mostrar un mensaje (Toast) de exito o error y luego navegar hacia atras.
    LaunchedEffect(taskOperationState) {
        when (val state = taskOperationState) {
            is TaskOperationState.Success -> {
                val message = if (taskToEdit == null) "Tarea creada con exito" else "Tarea actualizada con exito"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                taskViewModel.resetTaskOperationState()
                onBack()
            }
            is TaskOperationState.Error -> {
                Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                taskViewModel.resetTaskOperationState()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_agregar_tarea),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(if (taskToEdit == null) "Agregar Tarea" else "Editar Tarea", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BubbleCard {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título de la tarea") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                BubbleCard {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth().height(140.dp)
                    )
                }

                BubbleCard {
                    Column {
                        Text("Recordatorio", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { showDatePicker = true }, modifier = Modifier.weight(1f)) {
                                Text(text = "${day}/${month + 1}/${year}")
                            }
                            OutlinedTextField(
                                value = hour,
                                onValueChange = { if (it.length <= 2) hour = it.filter { c -> c.isDigit() } },
                                label = { Text("HH") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(0.5f)
                            )
                            OutlinedTextField(
                                value = minute,
                                onValueChange = { if (it.length <= 2) minute = it.filter { c -> c.isDigit() } },
                                label = { Text("MM") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(0.5f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val hourInt = hour.toIntOrNull()
                        val minuteInt = minute.toIntOrNull()

                        if (title.isBlank() || description.isBlank()) {
                            Toast.makeText(context, "Título y descripción son obligatorios.", Toast.LENGTH_LONG).show()
                            return@Button
                        }
                        if (hourInt == null || minuteInt == null || hourInt !in 0..23 || minuteInt !in 0..59) {
                            Toast.makeText(context, "Hora o minuto inválido.", Toast.LENGTH_LONG).show()
                            return@Button
                        }

                        val selectedDateTime = Calendar.getInstance().apply {
                            set(year, month, day, hourInt, minuteInt, 0)
                        }

                        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        val reminderString = formatter.format(selectedDateTime.time)

                        val now = Calendar.getInstance()
                        if (selectedDateTime.before(now)) {
                            Toast.makeText(context, "La fecha del recordatorio no puede ser en el pasado.", Toast.LENGTH_LONG).show()
                            return@Button
                        }

                        if (taskToEdit == null) {
                            taskViewModel.createTask(title, description, reminderString)
                        } else {
                            taskViewModel.updateTask(taskToEdit.id, title, description, reminderString, taskToEdit.estado)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = taskOperationState != TaskOperationState.Loading
                ) {
                    if (taskOperationState == TaskOperationState.Loading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text(
                            if (taskToEdit == null) "Guardar Tarea" else "Actualizar Tarea",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        // `rememberDatePickerState` crea y recuerda el estado para el DatePicker.
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)
        // `DatePickerDialog` es un dialogo pre-construido que contiene un calendario para seleccionar una fecha.
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val selectedCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = it }
                        year = selectedCalendar.get(Calendar.YEAR)
                        month = selectedCalendar.get(Calendar.MONTH)
                        day = selectedCalendar.get(Calendar.DAY_OF_MONTH)
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = datePickerState) }
    }
}


/**
 * Un Composable de ayuda que envuelve a su contenido en una tarjeta con fondo y bordes redondeados,
 * similar a una burbuja de chat.
 *
 * @param content El Composable que se mostrara dentro de la tarjeta.
 */
@Composable
fun BubbleCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.85f))
            .padding(16.dp)
    ) {
        content()
    }
}

/**
 * Este Composable parece ser un remanente de una version anterior o un componente no utilizado.
 * Se define aqui pero su cuerpo esta vacio, por lo que no dibuja nada en la pantalla.
 *
 * @param modifier Modificador de Compose.
 * @param title Titulo para la burbuja.
 * @param date Fecha para la burbuja.
 * @param isSelected Indica si esta seleccionada.
 * @param onClick Accion a ejecutar al pulsar.
 */
@Composable
fun DateBubble(
    modifier: Modifier = Modifier,
    title: String,
    date: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Vacio a proposito.
}