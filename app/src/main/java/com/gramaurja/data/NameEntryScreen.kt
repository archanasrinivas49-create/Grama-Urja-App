package com.gramaurja.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gramaurja.ui.theme.*

@Composable
fun NameEntryScreen(onNameSaved: (String) -> Unit) {

    var name by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    // Fade in animation
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800),
        label = "fade"
    )

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Logo / icon
            Text(text = "🌾", fontSize = 56.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome to\nGrama-Urja",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Power in Your Hand,\nBright Future for Our Land.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Name input card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Enter Your Name",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GramaGreen
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Your name will appear when you\nupdate the power status",
                        fontSize = 13.sp,
                        color = GramaGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Name text field
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            showError = false
                        },
                        label = { Text("Your Name") },
                        placeholder = { Text("e.g. Ravi Kumar") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        singleLine = true,
                        isError = showError,
                        supportingText = {
                            if (showError) {
                                Text(
                                    text = "Please enter your name to continue",
                                    color = GramaRed,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboard?.hide()
                                if (name.trim().isNotEmpty()) {
                                    onNameSaved(name.trim())
                                } else {
                                    showError = true
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GramaGreen,
                            focusedLabelColor = GramaGreen,
                            cursorColor = GramaGreen
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Continue button
                    Button(
                        onClick = {
                            keyboard?.hide()
                            if (name.trim().isNotEmpty()) {
                                onNameSaved(name.trim())
                            } else {
                                showError = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GramaGreen
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Text(
                            text = "Continue →",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "👥  Crowdsourced Power Monitor for Rural Areas",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

