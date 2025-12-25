package com.unap.aplicaciontareasfinal

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.unap.aplicaciontareasfinal.datastore.UserDataStore
import com.unap.aplicaciontareasfinal.ui.theme.AplicacionTareasFinalTheme
import com.unap.aplicaciontareasfinal.ui.theme.inicioSesion.LoginActivity
import com.unap.aplicaciontareasfinal.ui.theme.inicioSesion.MainActivity
import com.unap.aplicaciontareasfinal.ui.theme.inicioSesion.WelcomeActivity
import kotlinx.coroutines.flow.combine

class RootActivity : ComponentActivity() {

    private val userDataStore by lazy { UserDataStore(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AplicacionTareasFinalTheme {
                MainNavigation(userDataStore = userDataStore)
            }
        }
    }
}

@Composable
fun MainNavigation(userDataStore: UserDataStore) {
    val context = LocalContext.current
    val onboardingAndUserFlow = remember {
        userDataStore.hasSeenOnboarding.combine(userDataStore.getUser) { hasSeenOnboarding, user ->
            Pair(hasSeenOnboarding, user)
        }
    }

    val state by onboardingAndUserFlow.collectAsState(initial = Pair(null, null))

    val hasSeenOnboarding = state.first
    val user = state.second

    LaunchedEffect(hasSeenOnboarding, user) {
        if (hasSeenOnboarding != null) {
            val destination = when {
                !hasSeenOnboarding -> WelcomeActivity::class.java
                user == null -> LoginActivity::class.java
                else -> MainActivity::class.java
            }
            context.startActivity(Intent(context, destination))
            (context as? ComponentActivity)?.finish()
        }
    }

    // Show a loading indicator while we wait for the DataStore values
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
