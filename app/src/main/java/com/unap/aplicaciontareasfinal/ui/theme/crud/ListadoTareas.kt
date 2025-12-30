import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unap.aplicaciontareasfinal.R
import com.unap.aplicaciontareasfinal.data.Task
import com.unap.aplicaciontareasfinal.ui.theme.AplicacionTareasFinalTheme
import com.unap.aplicaciontareasfinal.viewmodel.TaskViewModel
import com.unap.aplicaciontareasfinal.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.*
import androidx.compose.ui.platform.LocalContext

/**
 * La anotacion @OptIn se usa para habilitar APIs experimentales.
 * - `ExperimentalMaterial3Api`: Para componentes de Material Design 3.
 * - `ExperimentalAnimationApi`: Para APIs de animacion como `AnimatedVisibility`.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
/**
 * Este es el Composable para la pantalla principal que muestra la lista de tareas del usuario.
 *
 * @param onLogoutClicked Funcion lambda que se ejecuta cuando el usuario pulsa el boton de cerrar sesion.
 * @param onAddTaskClicked Funcion lambda que se ejecuta para navegar a la pantalla de añadir tarea.
 * @param onEditTaskClicked Funcion lambda que se ejecuta para navegar a la pantalla de edicion, pasando la tarea a editar.
 * @param taskViewModel La instancia del ViewModel que gestiona la logica y el estado de las tareas.
 */
@Composable
fun TaskScreen(
    onLogoutClicked: () -> Unit,
    onAddTaskClicked: () -> Unit,
    onEditTaskClicked: (Task) -> Unit,
    taskViewModel: TaskViewModel
) {
    // `remember` se usa para mantener el estado a traves de las recomposiciones.
    // `SnackbarHostState` controla la visualizacion de Snackbars (mensajes emergentes).
    val snackbarHostState = remember { SnackbarHostState() }
    // `rememberCoroutineScope` obtiene un ambito de corrutina ligado al ciclo de vida del Composable,
    // usado aqui para lanzar la corrutina que muestra el Snackbar.
    val scope = rememberCoroutineScope()

    // `mutableStateOf` crea un estado observable. `searchText` almacena el texto de busqueda actual.
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    // `collectAsState` convierte un StateFlow del ViewModel en un State de Compose.
    // La UI se recompone automaticamente cuando estos valores cambian.
    val tasks by taskViewModel.tasks.collectAsState()
    val loading by taskViewModel.loading.collectAsState()
    val error by taskViewModel.error.collectAsState()

    // Estados locales para controlar la visibilidad de los dialogos.
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var taskToShowDetails by remember { mutableStateOf<Task?>(null) }

    // `LaunchedEffect(Unit)` ejecuta el bloque de corrutina una sola vez, cuando el Composable
    // se muestra por primera vez. Es ideal para cargar datos iniciales.
    LaunchedEffect(Unit) {
        taskViewModel.loadTasks()
    }

    // Este efecto se ejecuta cada vez que el valor de `error` cambia.
    // Se usa para mostrar un Snackbar cuando ocurre un error.
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = it,
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Short
                )
                taskViewModel.clearError() // Limpia el error para no mostrarlo de nuevo.
            }
        }
    }

    // `Box` es un contenedor basico que permite apilar elementos uno encima de otro.
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_tareas),
            contentDescription = "Fondo tareas",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // `SnackbarHost` es el componente que efectivamente muestra los Snackbars en la pantalla.
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

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
                // `LazyColumn` es para mostrar listas de forma eficiente.
                LazyColumn {
                    // `items` es el constructor para los elementos de la lista.
                    items(tasks) { task ->
                        TaskItem(
                            task = task,
                            onTaskCheckedChange = { checked ->
                                taskViewModel.updateTaskStatus(task, checked)
                            },
                            onEditTaskClicked = onEditTaskClicked,
                            onDeleteClicked = { taskToDelete = task },
                            onItemClicked = { taskToShowDetails = task }
                        )
                    }
                }
            }
        }
    }

    // Estos bloques `if` y `let` controlan la aparicion de los dialogos.
    // Cuando `taskToDelete` o `taskToShowDetails` no son nulos, el dialogo correspondiente se muestra.
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
                taskToDelete?.let { task ->
                    taskViewModel.deleteTask(task)
                }
                taskToDelete = null

                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Tarea eliminada",
                        duration = SnackbarDuration.Short
                    )
                }
            },
            onDismiss = { taskToDelete = null }
        )
    }

    taskToShowDetails?.let { task ->
        TaskDetailsDialog(task = task, onDismiss = { taskToShowDetails = null })
    }
}

/**
 * Representa un unico elemento (item) en la lista de tareas.
 *
 * @param task La tarea a mostrar.
 * @param onTaskCheckedChange Funcion que se llama cuando el estado del checkbox cambia.
 * @param onEditTaskClicked Funcion que se llama al pulsar el boton de editar.
 * @param onDeleteClicked Funcion que se llama al pulsar el boton de eliminar.
 * @param onItemClicked Funcion que se llama al pulsar sobre el item para ver detalles.
 */
@Composable
fun TaskItem(
    task: Task,
    onTaskCheckedChange: (Boolean) -> Unit,
    onEditTaskClicked: (Task) -> Unit,
    onDeleteClicked: () -> Unit,
    onItemClicked: () -> Unit
) {
    val deleteColor = if (task.estado) Color(0xFF2E7D32) else Color.Red
    val statusTextColor = if (task.estado) Color(0xFF2E7D32) else Color.Transparent

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onItemClicked() },
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

                IconButton(onClick = { onEditTaskClicked(task) }) {
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

            // `AnimatedVisibility` es un Composable que anima la aparicion y desaparicion de su contenido
            // basado en una condicion booleana.
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

/**
 * Un dialogo que muestra los detalles completos de una tarea.
 *
 * @param task La tarea cuyos detalles se van a mostrar.
 * @param onDismiss Funcion que se llama para cerrar el dialogo.
 */
@Composable
fun TaskDetailsDialog(
    task: Task,
    onDismiss: () -> Unit
) {
    // `Dialog` es un Composable que muestra contenido en una ventana emergente.
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = task.titulo,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                // Descripción
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = task.descripcion.ifEmpty { "Sin descripción." },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Recordatorio
                Text(
                    text = "Recordatorio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = task.recordatorio ?: "No hay recordatorio.", // Display reminder or default text
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Estado
                Text(
                    text = "Estado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    val statusText = if (task.estado) "Completada" else "Pendiente"
                    val statusColor = if (task.estado) Color(0xFF2E7D32) else Color.Red
                    val statusIcon = if (task.estado) Icons.Default.CheckCircle else Icons.Default.Info

                    Icon(
                        imageVector = statusIcon,
                        contentDescription = "Estado",
                        tint = statusColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = statusText,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Notificado
                Text(
                    text = "Notificación",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    val notifiedText = if (task.notificado == true) "Notificado" else "No notificado"
                    val notifiedColor = if (task.notificado == true) Color(0xFF2E7D32) else Color.Gray
                    val notifiedIcon = if (task.notificado == true) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff

                    Icon(
                        imageVector = notifiedIcon,
                        contentDescription = "Notificación",
                        tint = notifiedColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = notifiedText,
                        fontWeight = FontWeight.Bold,
                        color = notifiedColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}

/**
 * Un dialogo de confirmacion de eliminacion con animaciones.
 */
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
        // `AlertDialog` es un Composable estandar para dialogos de alerta.
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

/**
 * La anotacion `@Preview` permite ver una vista previa de este Composable en el editor de Android Studio
 * sin necesidad de ejecutar la aplicacion en un emulador o dispositivo.
 */
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
