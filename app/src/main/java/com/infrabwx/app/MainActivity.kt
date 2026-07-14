package com.infrabwx.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.infrabwx.app.ui.navigation.NavGraph
import com.infrabwx.app.ui.theme.InfraBWXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = application as InfraBwxApp
            val themeMode by app.preferences.themeMode.collectAsState(initial = "auto")
            val systemDark = isSystemInDarkTheme()
            val isDark = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> systemDark
            }

            InfraBWXTheme(darkTheme = isDark) {
                val colorScheme = MaterialTheme.colorScheme
                SideEffect {
                    window.statusBarColor = colorScheme.background.toArgb()
                    WindowCompat.getInsetsController(window, window.decorView)
                        .isAppearanceLightStatusBars = !isDark
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    var termsReady by remember { mutableStateOf(false) }
                    var termsAccepted by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        app.preferences.termsAccepted.collect { value ->
                            termsAccepted = value
                            termsReady = true
                        }
                    }

                    if (termsReady) {
                        NavGraph(
                            navController = navController,
                            hasAcceptedTerms = termsAccepted,
                            themeMode = themeMode,
                            preferences = app.preferences,
                            isDark = isDark
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}
