package com.unap.aplicaciontareasfinal

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.unap.aplicaciontareasfinal.ui.theme.inicioSesion.WelcomeActivity

class RootActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
