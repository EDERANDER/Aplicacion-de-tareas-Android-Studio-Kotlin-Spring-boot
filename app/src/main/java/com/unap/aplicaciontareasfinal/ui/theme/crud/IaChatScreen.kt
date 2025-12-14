package com.unap.aplicaciontareasfinal.ui.theme.crud

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.unap.aplicaciontareasfinal.R
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IaChatScreen() {

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var message by remember { mutableStateOf("") }

    val messages = remember {
        mutableStateListOf(
            ChatMessage("Hola, ¿en qué puedo ayudarte hoy?", false),
            ChatMessage("Necesito que me des recomendaciones sobre qué tarea hacer primero.", true),
            ChatMessage("La tarea más cercana que tienes es DESARROLLO DE PLATAFORMAS.", false),
            ChatMessage("Perfecto, gracias.", true)
        )
    }

    // 🔹 Auto scroll cuando llega un mensaje nuevo
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 🔹 Fondo
        Image(
            painter = painterResource(id = R.drawable.fondo_chat),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Asistente de Tareas",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    },
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
            ) {

                // 🔹 CHAT SCROLL
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { msg ->
                        ChatBubble(message = msg)
                    }
                }

                // 🔹 INPUT
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        placeholder = { Text("Escribe tu mensaje...") },
                        modifier = Modifier.weight(1f),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (message.isNotBlank()) {
                                messages.add(ChatMessage(message, true))
                                messages.add(
                                    ChatMessage(
                                        "Estoy analizando tus tareas y te daré la mejor recomendación 😉",
                                        false
                                    )
                                )
                                message = ""
                            }
                        }
                    ) {
                        Text("Enviar")
                    }
                }
            }
        }
    }
}

/* 🔹 Modelo del mensaje */
data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

/* 🔹 Burbuja de chat */
@Composable
fun ChatBubble(message: ChatMessage) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser)
            Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    if (message.isUser)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                    else
                        Color.White.copy(alpha = 0.9f)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = if (message.isUser) Color.White else Color.Black
            )
        }
    }
}
