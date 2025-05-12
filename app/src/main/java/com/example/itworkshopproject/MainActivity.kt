 package com.example.itworkshopproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.itworkshopproject.navigation.AppNavHost
import com.example.itworkshopproject.ui.theme.ITWorkshopProjectTheme
import com.example.itworkshopproject.ui.theme.LightColorScheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppContent()
        }
    }
}

@Composable
fun AppContent() {
    ITWorkshopProjectTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = LightColorScheme.background
        ) {
            AppNavHost()
        }
    }
}
