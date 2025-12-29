package com.unap.aplicaciontareasfinal.ui.theme.crud

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unap.aplicaciontareasfinal.R
import com.unap.aplicaciontareasfinal.viewmodel.ChatMessage
import com.unap.aplicaciontareasfinal.viewmodel.IaViewModel
import com.unap.aplicaciontareasfinal.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IaChatScreen(
    viewModel: IaViewModel = viewModel(factory = ViewModelFactory.getInstance(LocalContext.current))
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var message by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Auto scroll when a new message arrives
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(messages) { index, msg ->
                        ChatBubble(
                            message = msg,
                            isLastMessage = index == messages.size - 1
                        )
                    }
                    if (isLoading) {
                        item {
                            LoadingBubble()
                        }
                    }
                }

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
                    IconButton(
                        onClick = {
                            if (message.isNotBlank() && !isLoading) {
                                scope.launch {
                                    viewModel.sendMessage(message)
                                    message = ""
                                }
                            }
                        },
                        enabled = message.isNotBlank() && !isLoading
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar")
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, isLastMessage: Boolean) {
    var displayedText by remember { mutableStateOf("") }

    // Typing effect for AI messages
    LaunchedEffect(key1 = message.text) {
        if (!message.isFromUser && isLastMessage && !message.text.startsWith("Error:")) {
            message.text.forEachIndexed { index, _ ->
                displayedText = message.text.substring(0, index + 1)
                delay(35) // Adjust speed of typing
            }
        } else {
            displayedText = message.text
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    when {
                        message.text.startsWith("Error:") -> Color.Red.copy(alpha = 0.8f)
                        message.isFromUser -> MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                        else -> Color.White.copy(alpha = 0.9f)
                    }
                )
                .padding(12.dp)
        ) {
            Text(
                text = displayedText,
                color = if (message.isFromUser || message.text.startsWith("Error:")) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun LoadingBubble() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading-transition")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
        ), label = "alpha"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White.copy(alpha = alpha))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pensando...", color = Color.Black)
            }
        }
    }
}
