package com.unap.aplicaciontareasfinal.ui.theme.crud

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.unap.aplicaciontareasfinal.R
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.dp


@Composable
fun IaLoadingScreen(onTimeout: () -> Unit) {

    LaunchedEffect(Unit) {
        delay(2000) // ⏱ 2 segundos
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        /* 🔹 Fondo */
        Image(
            painter = painterResource(id = R.drawable.img_1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.6f
        )

        /* 🔹 Contenido */
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Asistente Inteligente",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            CircularProgressIndicator(
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Cargando...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}
