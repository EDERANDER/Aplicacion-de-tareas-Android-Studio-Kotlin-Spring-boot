package com.unap.aplicaciontareasfinal.ui.theme.crud

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unap.aplicaciontareasfinal.R
import com.unap.aplicaciontareasfinal.ui.theme.AplicacionTareasFinalTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.unit.sp
import androidx.compose.animation.*
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope





@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun TaskScreen(
    onAddTaskClicked: () -> Unit,
    onEditTaskClicked: () -> Unit

) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    val tasks = remember {
        mutableStateListOf(
            Task("Desarrollo de plataformas", false),
            Task("Llamar al ingeniero", false),
            Task("Trabajos para la universidad", true),
            Task("Completar la tarea de la semana", true),
            Task("Tarea de Cálculo Vectorial", false),
            Task("Concurso de programación", false)
        )
    }

    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    // 🔹 CONTENEDOR CON FONDO
    Box(modifier = Modifier.fillMaxSize()) {

        // 🔹 IMAGEN DE FONDO
        Image(
            painter = painterResource(id = R.drawable.fondo_tareas),
            contentDescription = "Fondo tareas",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )


        // 🔹 CONTENIDO
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {


        OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Buscar tareas") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddTaskClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Añadir tarea")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onTaskCheckedChange = { checked ->
                            val index = tasks.indexOf(task)
                            if (index != -1) {
                                tasks[index] = task.copy(isCompleted = checked)
                            }
                        },
                        onEditTaskClicked = onEditTaskClicked,
                        onDeleteClicked = { taskToDelete = task }
                    )
                }
            }
        }
    }

    if (taskToDelete != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
        )

        DeleteConfirmationDialog(
            title = "¿Eliminar esta tarea?",
            text = "estas seguro que quieres eliminar ?",
            onConfirm = {
                val deletedTask = taskToDelete
                tasks.remove(taskToDelete)
                taskToDelete = null

                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Tarea eliminada",
                        actionLabel = "Deshacer",
                        duration = SnackbarDuration.Short
                    )

                    if (result == SnackbarResult.ActionPerformed && deletedTask != null) {
                        tasks.add(deletedTask)
                    }
                }

            },
            onDismiss = { taskToDelete = null }
        )
    }

}

@Composable
fun TaskItem(
    task: Task,
    onTaskCheckedChange: (Boolean) -> Unit,
    onEditTaskClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {

    val deleteColor = if (task.isCompleted) Color(0xFF2E7D32) else Color.Red
    val statusTextColor = if (task.isCompleted) Color(0xFF2E7D32) else Color.Transparent

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = if (task.isCompleted)
            Color(0xFFE8F5E9).copy(alpha = 0.75f)
        else
            Color.White.copy(alpha = 0.60f),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onTaskCheckedChange
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = task.name,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                IconButton(onClick = onEditTaskClicked) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onDeleteClicked) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = deleteColor
                    )
                }
            }

            // 🔹 TEXTO "TAREA ENTREGADA"
            AnimatedVisibility(
                visible = task.isCompleted,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut()
            ) {
                Text(
                    text = "✔ Tarea entregada",
                    color = statusTextColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 40.dp, top = 6.dp)
                )
            }
        }
    }
}

data class Task(val name: String, val isCompleted: Boolean)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DeleteConfirmationDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = true,
        enter = scaleIn(initialScale = 0.8f) + fadeIn(),
        exit = scaleOut(targetScale = 0.8f) + fadeOut()
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            shape = RoundedCornerShape(24.dp),

            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Red
                    )
                }
            },

            text = {
                Text(
                    text = text,
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )
            },

            confirmButton = {
                Button(
                    onClick = onConfirm,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar", fontWeight = FontWeight.Bold)
                }
            },

            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTaskScreen() {
    AplicacionTareasFinalTheme {
        TaskScreen(onAddTaskClicked = {}, onEditTaskClicked = {})
    }
}
