package com.unap.aplicaciontareasfinal.ui.theme.crud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

sealed class Screen {
    object TaskList : Screen()
    object AddTask : Screen()
    object EditTask : Screen()
    object IaLoading : Screen()
    object IaIntro : Screen()
    object IaChat : Screen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val currentScreen = remember { mutableStateOf<Screen>(Screen.TaskList) }

    Scaffold(
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(onClick = { currentScreen.value = Screen.TaskList }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Home, contentDescription = "Home")
                            Text("Home")
                        }
                    }
                    IconButton(onClick = { currentScreen.value = Screen.IaLoading }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Search, contentDescription = "IA")
                            Text("IA")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (currentScreen.value) {
                is Screen.TaskList -> TaskScreen(
                    onAddTaskClicked = { currentScreen.value = Screen.AddTask },
                    onEditTaskClicked = { currentScreen.value = Screen.EditTask },
                )
                is Screen.AddTask -> AddTaskScreen(onBack = { currentScreen.value = Screen.TaskList })
                is Screen.EditTask -> EditTaskScreen(onBack = { currentScreen.value = Screen.TaskList })
                is Screen.IaLoading -> IaLoadingScreen(onTimeout = { currentScreen.value = Screen.IaIntro })
                is Screen.IaIntro -> IaIntroScreen(onStartChat = { currentScreen.value = Screen.IaChat })
                is Screen.IaChat -> IaChatScreen()
            }
        }
    }
}
