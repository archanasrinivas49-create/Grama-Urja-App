package com.gramaurja.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gramaurja.ui.theme.*

// ── Data model ────────────────────────────────────────────────────────────────

data class CropInfo(
    val name: String,
    val emoji: String,
    val minutesPerAcre: Int,      // pump runtime per acre (minutes)
    val waterLitresPerAcre: Int,  // water needed per acre (litres)
    val tip: String               // farming tip shown in result
)

val cropList = listOf(
    CropInfo(
        name               = "Tomato",
        emoji              = "🍅",
        minutesPerAcre     = 45,
        waterLitresPerAcre = 900,
        tip                = "Water tomatoes at the base. Avoid wetting leaves to prevent disease."
    ),
    CropInfo(
        name               = "Rice / Paddy",
        emoji              = "🌾",
        minutesPerAcre     = 90,
        waterLitresPerAcre = 2000,
        tip                = "Paddy needs standing water. Drain fields 2 weeks before harvest."
    ),
    CropInfo(
        name               = "Sugarcane",
        emoji              = "🎋",
        minutesPerAcre     = 120,
        waterLitresPerAcre = 2500,
        tip                = "Sugarcane needs heavy watering in the growing season. Reduce near harvest."
    ),
    CropInfo(
        name               = "Cotton",
        emoji              = "🌸",
        minutesPerAcre     = 60,
        waterLitresPerAcre = 1200,
        tip                = "Avoid overwatering cotton — waterlogging causes root rot."
    ),
    CropInfo(
        name               = "Groundnut",
        emoji              = "🥜",
        minutesPerAcre     = 45,
        waterLitresPerAcre = 800,
        tip                = "Groundnut needs moisture at flowering and pod-filling stages most."
    ),
    CropInfo(
        name               = "Vegetables",
        emoji              = "🥦",
        minutesPerAcre     = 30,
        waterLitresPerAcre = 500,
        tip                = "Water vegetables in the early morning to reduce evaporation loss."
    ),
    CropInfo(
        name               = "Wheat",
        emoji              = "🌿",
        minutesPerAcre     = 50,
        waterLitresPerAcre = 900,
        tip                = "Wheat needs 4–6 irrigations. Critical stages: crown root, tillering, grain fill."
    ),
    CropInfo(
        name               = "Maize",
        emoji              = "🌽",
        minutesPerAcre     = 40,
        waterLitresPerAcre = 750,
        tip                = "Maize is sensitive to drought at tasselling stage — never skip that watering."
    ),
    CropInfo(
        name               = "Banana",
        emoji              = "🍌",
        minutesPerAcre     = 100,
        waterLitresPerAcre = 2200,
        tip                = "Banana needs frequent watering. Drip irrigation is most efficient."
    ),
    CropInfo(
        name               = "Onion",
        emoji              = "🧅",
        minutesPerAcre     = 35,
        waterLitresPerAcre = 600,
        tip                = "Stop watering onions 2 weeks before harvest to improve shelf life."
    )
)

// ── Screen ─────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PumpTimerScreen(onBack: () -> Unit) {

    var selectedCrop by remember { mutableStateOf(cropList[0]) }
    var acres        by remember { mutableStateOf(2f) }
    var showResult   by remember { mutableStateOf(false) }

    // Computed values
    val totalMinutes = (selectedCrop.minutesPerAcre * acres).toInt()
    val hours        = totalMinutes / 60
    val mins         = totalMinutes % 60
    val totalWater   = (selectedCrop.waterLitresPerAcre * acres).toInt()
    val timeLabel    = if (hours > 0) "${hours}h ${mins}m" else "${mins} minutes"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GramaLightGray)
    ) {
        // Top bar
        TopAppBar(
            title = {
                Text(
                    text = "Pump Timer",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 20.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = GramaGreen)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── Header card ─────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GramaGreen)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "⏱", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Pump Timer Calculator",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Save electricity • Avoid water wastage • Better irrigation",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // ── Crop selector card ───────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionLabel(text = "Step 1 — Select Crop Type")
                    Spacer(modifier = Modifier.height(12.dp))

                    // 2-column grid of crop chips
                    val rows = cropList.chunked(2)
                    rows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { crop ->
                                val selected = crop == selectedCrop
                                CropChip(
                                    crop = crop,
                                    selected = selected,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        selectedCrop = crop
                                        showResult = false
                                    }
                                )
                            }
                            // Fill empty cell in last row if odd count
                            if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // ── Land size card ───────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionLabel(text = "Step 2 — Enter Land Size")
                    Spacer(modifier = Modifier.height(12.dp))

                    // Acre display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Field Size",
                            fontSize = 14.sp,
                            color = GramaGray
                        )
                        Box(
                            modifier = Modifier
                                .background(GramaGreen, RoundedCornerShape(8.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${String.format("%.1f", acres)} Acres",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = acres,
                        onValueChange = {
                            acres = it
                            showResult = false
                        },
                        valueRange = 0.5f..20f,
                        steps = 38,
                        colors = SliderDefaults.colors(
                            thumbColor = GramaGreen,
                            activeTrackColor = GramaGreen,
                            inactiveTrackColor = GramaGreen.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("0.5 acres", fontSize = 11.sp, color = GramaGray)
                        Text("20 acres", fontSize = 11.sp, color = GramaGray)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Water requirement info row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                GramaGreen.copy(alpha = 0.06f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💧", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Water Requirement",
                                fontSize = 11.sp,
                                color = GramaGray
                            )
                            Text(
                                text = "$totalWater litres for ${String.format("%.1f", acres)} acres of ${selectedCrop.name}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GramaDarkText
                            )
                        }
                    }
                }
            }

            // ── Calculate button ─────────────────────────────────────────────
            Button(
                onClick = { showResult = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GramaGreen),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(text = "⚡", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CALCULATE PUMP TIME",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }

            // ── Result card ──────────────────────────────────────────────────
            AnimatedVisibility(
                visible = showResult,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 2 },
                exit  = fadeOut()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    // Main result
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = GramaGreen),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Recommended Pump Time",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = timeLabel,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "for ${String.format("%.1f", acres)} acres of ${selectedCrop.name}",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Summary details card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Summary",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GramaDarkText
                            )
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                            SummaryRow("Crop",        "${selectedCrop.emoji}  ${selectedCrop.name}")
                            SummaryRow("Land Size",   "${String.format("%.1f", acres)} Acres")
                            SummaryRow("Pump Time",   timeLabel)
                            SummaryRow("Water Needed","$totalWater litres")
                        }
                    }

                    // Farming tip card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = GramaYellow.copy(alpha = 0.12f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "💡", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Farming Tip",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5D4037)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = selectedCrop.tip,
                                    fontSize = 13.sp,
                                    color = Color(0xFF5D4037),
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }

                    // Savings tip card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = GramaGreenAccent.copy(alpha = 0.08f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "⚡ Electricity Saving Tips",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GramaGreen
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TipBullet("Run pump only when power is confirmed ON in your zone")
                            TipBullet("Water during early morning (6–8 AM) to reduce evaporation")
                            TipBullet("Stop pump exactly at $timeLabel to avoid over-irrigation")
                            TipBullet("Fix leaks in pipes — even small leaks waste 100s of litres")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ── Small composables ──────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = GramaGreen
    )
}

@Composable
private fun CropChip(
    crop: CropInfo,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (selected) GramaGreen else GramaGreen.copy(alpha = 0.07f)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = crop.emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = crop.name.split(" ")[0],
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) Color.White else GramaDarkText,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = GramaGray)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = GramaDarkText)
    }
}

@Composable
private fun TipBullet(text: String) {
    Row(
        modifier = Modifier.padding(bottom = 5.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(GramaGreen)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = GramaGreen,
            lineHeight = 17.sp
        )
    }
}