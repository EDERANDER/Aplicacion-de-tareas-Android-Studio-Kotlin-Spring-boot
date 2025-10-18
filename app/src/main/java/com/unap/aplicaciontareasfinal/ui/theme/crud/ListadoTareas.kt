
package com.unap.aplicaciontareasfinal.ui.theme.crud

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unap.aplicaciontareasfinal.ui.theme.AplicacionTareasFinalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(onAddTaskClicked: () -> Unit, onEditTaskClicked: () -> Unit) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val tasks = remember {
        mutableStateListOf(
            Task("Desarrollo de plataformas", false),
            Task("Llamar al ingeniero", false),
            Task("Trabajos para la universidad", true),
            Task("Completar la tarea de la semana", true),
            Task("Tarea de Calculo Vectorial", false),
            Task("Concurso de programación", false)
        )
    }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Buscar tareas") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onAddTaskClicked() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir tarea")
        }
        Spacer(modifier = Modifier.height(16.dp))
        AssistChip(
            onClick = { /* TODO */ },
            label = { Text("Tareas sin hacer") },
            leadingIcon = {
                Checkbox(
                    checked = false,
                    onCheckedChange = null
                )
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(tasks) { task ->
                if (task == taskToDelete) {
                    Button(
                        onClick = { taskToDelete = null },
                        colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirmar Eliminación")
                    }
                } else {
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
}

@Composable
fun TaskItem(
    task: Task,
    onTaskCheckedChange: (Boolean) -> Unit,
    onEditTaskClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = onTaskCheckedChange
        )
        Text(
            text = task.name,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { onEditTaskClicked() }) {
            Icon(Icons.Default.Edit, contentDescription = "Editar")
        }
        IconButton(onClick = { onDeleteClicked() }) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
        }
    }
}

data class Task(val name: String, val isCompleted: Boolean)

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AplicacionTareasFinalTheme {
        TaskScreen(onAddTaskClicked = {}, onEditTaskClicked = {})
    }
}
