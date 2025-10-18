package com.unap.aplicaciontareasfinal.ui.theme.crud

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unap.aplicaciontareasfinal.ui.theme.AplicacionTareasFinalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(onBack: () -> Unit) {
    var title by remember { mutableStateOf("Desarrollo de plataformas") }
    var description by remember { mutableStateOf("Esta tarea que dejo el ing, trata sobre el desarrollo de aplicaciones moviles en android studio utilizando kotlin.") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Tarea") },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Inicio")
                    Text("mart 9 sept")
                }
                Column {
                    Text("Final")
                    Text("mier 16 sep")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onBack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Actualizar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditTaskScreenPreview() {
    AplicacionTareasFinalTheme {
        EditTaskScreen(onBack = {})
    }
}
