package com.unap.aplicaciontareasfinal.ui.theme.crud

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
import com.unap.aplicaciontareasfinal.ui.theme.inicioSesion.LoginActivity
import com.unap.aplicaciontareasfinal.ui.theme.perfil.ProfileScreen
import com.unap.aplicaciontareasfinal.viewmodel.LogoutState
import com.unap.aplicaciontareasfinal.viewmodel.TaskViewModel

sealed class Screen {
    object TaskList : Screen()
    object AddTask : Screen()
    object EditTask : Screen()
    object IaLoading : Screen()
    object IaIntro : Screen()
    object IaChat : Screen()
    object Profile : Screen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(viewModel: TaskViewModel) {
    val currentScreen = remember { mutableStateOf<Screen>(Screen.TaskList) }
    val logoutState by viewModel.logoutState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(logoutState) {
        if (logoutState is LogoutState.Success) {
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
                    selected = currentScreen.value is Screen.IaLoading || currentScreen.value is Screen.IaIntro || currentScreen.value is Screen.IaChat,
                    onClick = { currentScreen.value = Screen.IaLoading }
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
                    onLogoutClicked = { viewModel.logout() },
                    onAddTaskClicked = { currentScreen.value = Screen.AddTask },
                    onEditTaskClicked = { currentScreen.value = Screen.EditTask },
                )

                is Screen.AddTask -> AddTaskScreen(onBack = {
                    currentScreen.value = Screen.TaskList
                })

                is Screen.EditTask -> EditTaskScreen(onBack = {
                    currentScreen.value = Screen.TaskList
                })

                is Screen.IaLoading -> IaLoadingScreen(onTimeout = {
                    currentScreen.value = Screen.IaIntro
                })

                is Screen.IaIntro -> IaIntroScreen(onStartChat = {
                    currentScreen.value = Screen.IaChat
                })

                is Screen.IaChat -> IaChatScreen()
                is Screen.Profile -> ProfileScreen(onLogoutClicked = { viewModel.logout() })
            }
        }
    }
}
