package com.yourpackage.codexterm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.yourpackage.codexterm.ui.CodexTermApp
import com.yourpackage.codexterm.ui.theme.CodexTermTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = AppContainer(applicationContext)
        setContent {
            CodexTermTheme {
                CodexTermApp(appContainer = appContainer)
            }
        }
    }
}
