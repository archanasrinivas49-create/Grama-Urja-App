package com.gramaurja.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Brand Colors - matching the image exactly
val GramaGreen = Color(0xFF1B5E20)         // Dark forest green (primary)
val GramaGreenLight = Color(0xFF2E7D32)    // Medium green
val GramaGreenAccent = Color(0xFF4CAF50)   // Button green
val GramaRed = Color(0xFFC62828)           // Power OFF red
val GramaRedAccent = Color(0xFFD32F2F)     // Button red
val GramaWhite = Color(0xFFFFFFFF)
val GramaLightGray = Color(0xFFF5F5F5)
val GramaGray = Color(0xFF757575)
val GramaDarkText = Color(0xFF212121)
val GramaYellow = Color(0xFFFFC107)        // Tagline yellow

private val ColorScheme = lightColorScheme(
    primary = GramaGreen,
    secondary = GramaGreenAccent,
    error = GramaRed,
    background = GramaWhite,
    surface = GramaWhite,
    onPrimary = GramaWhite,
    onSecondary = GramaWhite,
    onBackground = GramaDarkText,
    onSurface = GramaDarkText,
)

@Composable
fun GramaUrjaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        content = content
    )
}
