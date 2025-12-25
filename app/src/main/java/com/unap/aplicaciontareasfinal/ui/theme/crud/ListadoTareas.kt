package com.unap.aplicaciontareasfinal.ui.theme.crud

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unap.aplicaciontareasfinal.data.Task
import com.unap.aplicaciontareasfinal.viewmodel.TaskViewModel
import com.unap.aplicaciontareasfinal.viewmodel.ViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    onLogoutClicked: () -> Unit,
    onAddTaskClicked: () -> Unit,
    onEditTaskClicked: () -> Unit,
    taskViewModel: TaskViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    val tasks by taskViewModel.tasks.collectAsState()
    val loading by taskViewModel.loading.collectAsState()
    val error by taskViewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        taskViewModel.loadTasks()
    }

    // Show snackbar for errors
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = it,
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Short
                )
                taskViewModel.clearError()
            }
        }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Buscar tareas") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onLogoutClicked) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddTaskClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Añadir tarea")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (tasks.isEmpty()) {
                Text("No hay tareas para mostrar.", modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn {
                    items(tasks) { task ->
                        TaskItem(
                            task = task,
                            onTaskCheckedChange = { checked ->
                                // TODO: Implement task completion update in ViewModel
                            },
                            onEditTaskClicked = onEditTaskClicked,
                            onDeleteClicked = { taskToDelete = task }
                        )
                    }
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
                // TODO: Implement actual task deletion in ViewModel
                taskToDelete = null

                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Tarea eliminada",
                        actionLabel = "Deshacer",
                        duration = SnackbarDuration.Short
                    )

                    // if (result == SnackbarResult.ActionPerformed && deletedTask != null) {
                    //    tasks.add(deletedTask) // This logic needs to be in ViewModel
                    // }
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

    val deleteColor = if (task.estado) Color(0xFF2E7D32) else Color.Red
    val statusTextColor = if (task.estado) Color(0xFF2E7D32) else Color.Transparent

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = if (task.estado)
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
                    checked = task.estado,
                    onCheckedChange = onTaskCheckedChange
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = task.titulo,
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
                visible = task.estado,
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
    val context = LocalContext.current
    val factory = ViewModelFactory.getInstance(context)
    val viewModel: TaskViewModel = viewModel(factory = factory)
    AplicacionTareasFinalTheme {
        TaskScreen(
            onLogoutClicked = {},
            onAddTaskClicked = {},
            onEditTaskClicked = {},
            taskViewModel = viewModel
        )
    }
}
