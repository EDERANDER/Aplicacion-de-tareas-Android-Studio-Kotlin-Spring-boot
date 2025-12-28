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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    taskViewModel: TaskViewModel,
    onBack: () -> Unit,
    taskToEdit: Task? = null
) {
    val context = LocalContext.current
    val taskOperationState by taskViewModel.taskOperationState.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    var year by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var month by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var day by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }

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

    LaunchedEffect(taskOperationState) {
        when (val state = taskOperationState) {
            is TaskOperationState.Success -> {
                val message = if (taskToEdit == null) "Tarea creada con éxito" else "Tarea actualizada con éxito"
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
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)
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

// Adding this function back to satisfy the compiler, even if it's not used by AddTaskScreen
@Composable
fun DateBubble(
    modifier: Modifier = Modifier,
    title: String,
    date: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Empty on purpose
}