package com.example.itworkshopproject.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.itworkshopproject.R
import com.example.itworkshopproject.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    var visible by remember { mutableStateOf(false) }

    val scale = animateFloatAsState(
        targetValue = if (visible) 1f else 0.5f,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutBounce),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(3000) // Delay before navigating
        navController.navigate(Routes.SignIn.route) {
            popUpTo(Routes.SplashScreen.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF1E1E2E), Color(0xFF2B2B4C))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.picture),
                contentDescription = "SoulSync Logo",
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale.value)
            )

            Spacer(modifier = Modifier.height(20.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(1000)),
                exit = fadeOut()
            ) {
                Text(
                    text = "LET'S CONNECT",
                    fontSize = 20.sp,
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFD700), // Gold
                                Color(0xFFFFA500), // Orange
                                Color(0xFFFF4500)  // Orange Red
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(300f, 100f),
                            tileMode = TileMode.Clamp
                        )
                    )
                )
            }
        }
    }
}
