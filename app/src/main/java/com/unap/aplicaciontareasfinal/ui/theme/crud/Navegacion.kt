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

sealed class Screen {
    object TaskList : Screen()
    object AddTask : Screen()
    data class EditTask(val task: Task) : Screen()
    object IaChat : Screen()
    object Profile : Screen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(taskViewModel: TaskViewModel) {
    val currentScreen = remember { mutableStateOf<Screen>(Screen.TaskList) }
    val logoutState by taskViewModel.logoutState.collectAsState()
    val currentUser by taskViewModel.user.collectAsState() // Obtener el usuario
    val context = LocalContext.current
    val factory = ViewModelFactory.getInstance(context)
    val profileViewModel: ProfileViewModel = viewModel(factory = factory)
    val userDeletionState by profileViewModel.userDeletionState.collectAsState()

    LaunchedEffect(logoutState) {
        if (logoutState is LogoutState.Success) {
            val intent = Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
            (context as? Activity)?.finish()
        }
    }

    LaunchedEffect(userDeletionState) {
        if (userDeletionState is UserDeletionState.Success) {
            val intent = Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
            (context as? Activity)?.finish()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentScreen.value is Screen.TaskList || currentScreen.value is Screen.AddTask || currentScreen.value is Screen.EditTask,
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
        Column(modifier = Modifier.padding(paddingValues)) {
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

