package com.gramaurja.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
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
import com.gramaurja.data.PowerStatus
import com.gramaurja.data.Zone
import com.gramaurja.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectedZone : Zone,
    powerStatus  : PowerStatus,
    farmerName   : String = "Farmer",
    onPowerOn    : () -> Unit,
    onPowerOff   : () -> Unit,
    onSelectZone : () -> Unit,
    onPumpTimer  : () -> Unit = {}
) {
    val isOn = powerStatus.isOn

    val bgColor by animateColorAsState(
        targetValue   = if (isOn) GramaGreenAccent else GramaRed,
        animationSpec = tween(600),
        label         = "bgColor"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.2f,
        targetValue   = 0.5f,
        animationSpec = infiniteRepeatable(
            animation  = tween(900, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    var refreshTick by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(30_000)
            refreshTick++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GramaLightGray)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text       = "Grama-Urja",
                        fontWeight = FontWeight.Bold,
                        color      = Color.White,
                        fontSize   = 17.sp
                    )
                    Text(
                        text     = "Hello, $farmerName 👋",
                        fontSize = 11.sp,
                        color    = Color.White.copy(alpha = 0.85f)
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onSelectZone) {
                    Icon(Icons.Default.Menu, contentDescription = "Zones", tint = Color.White)
                }
            },
            actions = {
                IconButton(onClick = { refreshTick++ }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = GramaGreen)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Selected Zone", fontSize = 11.sp, color = GramaGray)
                Text(
                    text       = selectedZone.name,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = GramaDarkText
                )
                Text(
                    text     = selectedZone.transformerZone,
                    fontSize = 11.sp,
                    color    = GramaGray
                )
            }

            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(14.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                            .background(bgColor)
                            .padding(vertical = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = pulseAlpha))
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text          = "POWER STATUS",
                                fontSize      = 10.sp,
                                color         = Color.White.copy(alpha = 0.85f),
                                fontWeight    = FontWeight.SemiBold,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            AnimatedContent(
                                targetState    = if (isOn) "ON" else "OFF",
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label          = "statusText"
                            ) { status ->
                                Text(
                                    text       = status,
                                    fontSize   = 44.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color      = Color.White
                                )
                            }
                            Text(
                                text       = if (isOn) "Power is Available" else "Power is Not Available",
                                fontSize   = 12.sp,
                                color      = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val lastSeen = remember(refreshTick, powerStatus) {
                            powerStatus.lastSeenText()
                        }
                        CompactInfoRow("🕐", "Last Seen",  lastSeen)
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        CompactInfoRow("👤", "Updated By", powerStatus.updatedBy)
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        Row(
                            modifier          = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "✅", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = "Freshness", fontSize = 10.sp, color = GramaGray)
                                Text(
                                    text       = powerStatus.freshnessLabel(),
                                    fontSize   = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = powerStatus.freshnessColor()
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick   = onPowerOn,
                modifier  = Modifier.fillMaxWidth().height(50.dp),
                shape     = RoundedCornerShape(10.dp),
                colors    = ButtonDefaults.buttonColors(containerColor = GramaGreenAccent),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
            ) {
                Text(
                    text          = "⚡  POWER ON",
                    fontSize      = 15.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = Color.White,
                    letterSpacing = 1.sp
                )
            }

            Button(
                onClick   = onPowerOff,
                modifier  = Modifier.fillMaxWidth().height(50.dp),
                shape     = RoundedCornerShape(10.dp),
                colors    = ButtonDefaults.buttonColors(containerColor = GramaRed),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
            ) {
                Text(
                    text          = "🔴  POWER OFF",
                    fontSize      = 15.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = Color.White,
                    letterSpacing = 1.sp
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(8.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = GramaGreen.copy(alpha = 0.07f)
                )
            ) {
                Text(
                    text      = "📡  Updates sync with all users instantly.",
                    fontSize  = 11.sp,
                    color     = GramaGreen,
                    modifier  = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center
                )
            }

            OutlinedButton(
                onClick  = onPumpTimer,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape    = RoundedCornerShape(10.dp),
                border   = androidx.compose.foundation.BorderStroke(1.5.dp, GramaGreen),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = GramaGreen)
            ) {
                Text(text = "⏱", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text          = "PUMP TIMER CALCULATOR",
                    fontSize      = 13.sp,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CompactInfoRow(icon: String, label: String, value: String) {
    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label, fontSize = 10.sp, color = GramaGray)
            Text(
                text       = value,
                fontSize   = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color      = GramaDarkText
            )
        }
    }
}