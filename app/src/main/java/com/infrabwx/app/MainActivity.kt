package com.infrabwx.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.infrabwx.app.data.preferences.AppPreferences
import com.infrabwx.app.ui.navigation.NavGraph
import com.infrabwx.app.ui.theme.InfraBWXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InfraBWXTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val app = application as InfraBwxApp
                    val termsAccepted by app.preferences.termsAccepted.collectAsState(initial = false)

                    NavGraph(
                        navController = navController,
                        hasAcceptedTerms = termsAccepted
                    )
                }
            }
        }
    }
}
