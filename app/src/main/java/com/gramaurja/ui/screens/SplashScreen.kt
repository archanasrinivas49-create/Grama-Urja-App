package com.gramaurja.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gramaurja.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {

    // Pulse animation for the logo circle
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Dot indicators (pager dots)
    var activeDot by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        repeat(3) {
            delay(700)
            activeDot = it
        }
        delay(800)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GramaGreen, GramaGreenLight)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {

            // Logo circle
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    // Bulb icon using text emoji as placeholder
                    // In real app, replace with your vector drawable
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "💡", fontSize = 40.sp)
                        Text(
                            text = "🌿",
                            fontSize = 20.sp,
                            modifier = Modifier.offset(y = (-6).dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App name
            Text(
                text = "Grama-Urja",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Crowdsourced Power Monitor\nfor Rural Areas",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Tagline
            Text(
                text = "Power in Your Hand,",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic
            )
            Text(
                text = "Bright Future for Our Land.",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = GramaYellow,
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Dot indicators
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == activeDot) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == activeDot) Color.White
                                else Color.White.copy(alpha = 0.4f)
                            )
                    )
                }
            }
        }
    }
}


