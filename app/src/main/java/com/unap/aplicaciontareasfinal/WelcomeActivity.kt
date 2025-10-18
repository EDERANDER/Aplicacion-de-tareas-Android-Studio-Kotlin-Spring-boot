package com.unap.aplicaciontareasfinal

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unap.apptareas.ui.theme.AppTareasTheme

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTareasTheme {
                WelcomeScreen(
                    onStartClick = {
                        val intent = Intent(this, TermsActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun WelcomeScreen(onStartClick: () -> Unit) {
    val gradientColors = listOf(
        Color(0xFFC2E9FB),
        Color(0xFFA1C4FD)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(gradientColors)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {

            Text(
                text = "Worki Work",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3A2B8A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Organiza. Completa. Triunfa.",
                fontSize = 18.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.illustration_students),
                contentDescription = "Bienvenida",
                modifier = Modifier.size(250.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onStartClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "Comenzar",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}