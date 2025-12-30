import com.unap.aplicaciontareasfinal.data.Task
import com.unap.aplicaciontareasfinal.ui.theme.crud.AddTaskScreen
import com.unap.aplicaciontareasfinal.ui.theme.crud.IaChatScreen
import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unap.aplicaciontareasfinal.ui.theme.inicioSesion.LoginActivity
import com.unap.aplicaciontareasfinal.ui.theme.perfil.ProfileScreen
import com.unap.aplicaciontareasfinal.viewmodel.LogoutState
import com.unap.aplicaciontareasfinal.viewmodel.ProfileViewModel
import com.unap.aplicaciontareasfinal.viewmodel.TaskViewModel
import com.unap.aplicaciontareasfinal.viewmodel.UserDeletionState
import com.unap.aplicaciontareasfinal.viewmodel.ViewModelFactory

/**
 * Una `sealed class` (clase sellada) es una clase especial en Kotlin que restringe la jerarquia de herencia.
 * Todas las subclases de una clase sellada deben estar declaradas en el mismo archivo.
 * Esto es muy util para manejar estados o, como en este caso, para representar un conjunto finito
 * y conocido de pantallas en la aplicacion, lo que hace que el manejo en un `when` sea mas seguro.
 */
sealed class Screen {
    object TaskList : Screen()
    object AddTask : Screen()
    data class EditTask(val task: Task) : Screen()
    object IaChat : Screen()
    object Profile : Screen()
}

/**
 * La anotacion @OptIn se usa para habilitar el uso de APIs que el creador ha marcado como experimentales.
 * En este caso, habilita componentes de Material Design 3 que aun podrian cambiar en el futuro.
 */
@OptIn(ExperimentalMaterial3Api::class)
/**
 * Este es el Composable principal de navegacion de la aplicacion despues de que el usuario inicia sesion.
 * Gestiona la pantalla que se muestra actualmente y la barra de navegacion inferior.
 *
 * @param taskViewModel La instancia del ViewModel principal para las tareas. Se pasa desde la actividad que lo contiene.
 */
@Composable
fun AppNavigation(taskViewModel: TaskViewModel) {
    // `remember` y `mutableStateOf` se usan para crear un estado que sobrevive a las recomposiciones.
    // `currentScreen` almacena cual de las pantallas definidas en la clase `Screen` se esta mostrando.
    val currentScreen = remember { mutableStateOf<Screen>(Screen.TaskList) }
    val logoutState by taskViewModel.logoutState.collectAsState()
    val currentUser by taskViewModel.user.collectAsState() // Obtener el usuario

    // `LocalContext.current` es la forma en Compose de obtener el contexto de Android,
    // necesario para inicializar la ViewModelFactory o para crear Intents.
    val context = LocalContext.current

    // Obtiene la instancia unica (singleton) de nuestra factoria de ViewModels.
    val factory = ViewModelFactory.getInstance(context)
    // `viewModel()` es la funcion de Compose para obtener una instancia de un ViewModel.
    // Le pasamos nuestra factoria personalizada para que sepa como crear el `ProfileViewModel`
    // con sus dependencias (UserService, TaskService, etc.).
    val profileViewModel: ProfileViewModel = viewModel(factory = factory)
    val userDeletionState by profileViewModel.userDeletionState.collectAsState()

    // `LaunchedEffect` ejecuta una corrutina cuando el Composable entra en la composicion.
    // Si la `key` (logoutState) cambia, la corrutina se relanza.
    // Aqui se usa para observar el estado de cierre de sesion y navegar a LoginActivity cuando es exitoso.
    LaunchedEffect(logoutState) {
        if (logoutState is LogoutState.Success) {
            val intent = Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
            (context as? Activity)?.finish()
        }
    }

    // Este segundo `LaunchedEffect` observa el estado de eliminacion del usuario.
    // Si el usuario se elimina con exito desde la pantalla de perfil, tambien se navega a la pantalla de Login.
    LaunchedEffect(userDeletionState) {
        if (userDeletionState is UserDeletionState.Success) {
            val intent = Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
            (context as? Activity)?.finish()
        }
    }

    // `Scaffold` es un componente que implementa la estructura visual basica de Material Design.
    // Proporciona ranuras (slots) para componentes comunes como una barra superior (TopBar) o
    // una barra de navegacion inferior (BottomBar).
    Scaffold(
        bottomBar = {
            // `NavigationBar` es el contenedor para la barra de navegacion inferior.
            NavigationBar {
                // Cada `NavigationBarItem` representa un boton en la barra de navegacion.
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    // Determina si este item esta seleccionado (resaltado).
                    selected = currentScreen.value is Screen.TaskList || currentScreen.value is Screen.AddTask || currentScreen.value is Screen.EditTask,
                    // La accion a ejecutar cuando se presiona el item.
                    onClick = { currentScreen.value = Screen.TaskList }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.AutoAwesome, contentDescription = "IA") },
                    label = { Text("IA") },
                    selected = currentScreen.value is Screen.IaChat,
                    onClick = { currentScreen.value = Screen.IaChat }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") },
                    selected = currentScreen.value is Screen.Profile,
                    onClick = { currentScreen.value = Screen.Profile }
                )
            }
        }
    ) { paddingValues ->
        // El contenido principal de la pantalla se coloca aqui. El `paddingValues` es proporcionado
        // por el Scaffold para asegurar que el contenido no se superponga con las barras.
        Column(modifier = Modifier.padding(paddingValues)) {
            // El `when` en Kotlin es como un `switch` en otros lenguajes.
            // Aqui se usa para decidir que Composable mostrar basado en el valor de `currentScreen`.
            when (currentScreen.value) {
                is Screen.TaskList -> TaskScreen(
                    onLogoutClicked = { taskViewModel.logout() },
                    onAddTaskClicked = { currentScreen.value = Screen.AddTask },
                    onEditTaskClicked = { task -> currentScreen.value = Screen.EditTask(task) },
                    taskViewModel = taskViewModel
                )

                is Screen.AddTask -> AddTaskScreen(
                    taskViewModel = taskViewModel,
                    onBack = {
                        currentScreen.value = Screen.TaskList
                    }
                )

                is Screen.EditTask -> {
                    val taskToEdit = (currentScreen.value as Screen.EditTask).task
                    AddTaskScreen(
                        taskViewModel = taskViewModel,
                        onBack = { currentScreen.value = Screen.TaskList },
                        taskToEdit = taskToEdit
                    )
                }
                is Screen.IaChat -> IaChatScreen()
                is Screen.Profile -> ProfileScreen(
                    user = currentUser,
                    onLogoutClicked = { taskViewModel.logout() },
                    profileViewModel = profileViewModel
                )
            }
        }
    }
}

