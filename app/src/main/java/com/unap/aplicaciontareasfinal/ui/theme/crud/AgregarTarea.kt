package com.unap.aplicaciontareasfinal.ui.theme.crud

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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

/* 🔹 Formateo seguro (evita restar 1 día) */
fun formatDateSafe(millis: Long, formatter: SimpleDateFormat): String {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 12)
    }
    return formatter.format(calendar.time)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(onBack: () -> Unit) {

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val formatter = remember {
        SimpleDateFormat("dd 'de' MMMM yyyy", Locale("es", "PE"))
    }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

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

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Agregar Tarea", fontWeight = FontWeight.Bold) },
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

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { it / 2 }
                ) {
                    BubbleCard {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Título de la tarea") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
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
                        scope.launch {
                            when {
                                title.isBlank() ->
                                    snackbarHostState.showSnackbar("El título es obligatorio")

                                startMillis == null || endMillis == null ->
                                    snackbarHostState.showSnackbar("Selecciona ambas fechas")

                                startMillis!! > endMillis!! ->
                                    snackbarHostState.showSnackbar("La fecha de inicio no puede ser mayor")

                                else -> onBack()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Guardar Tarea", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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

/* 🔹 Burbuja reutilizable */
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

/* 🔹 Burbuja de fecha (círculo → rectángulo) */
@Composable
fun DateBubble(
    modifier: Modifier = Modifier,
    title: String,
    date: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(
                if (isSelected)
                    RoundedCornerShape(16.dp)
                else
                    RoundedCornerShape(50)
            )
            .background(Color.White.copy(alpha = 0.85f))
            .clickable { onClick() }
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(date, color = MaterialTheme.colorScheme.primary)
        }
    }
}
