package com.gramaurja.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gramaurja.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateConfirmationScreen(
    zoneName   : String,
    newStatus  : Boolean,
    farmerName : String = "You",
    onOk       : () -> Unit
) {
    var started by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue  = if (started) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium
        ),
        label = "check"
    )
    LaunchedEffect(Unit) { started = true }

    val timestamp = remember {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GramaWhite)
    ) {
        TopAppBar(
            title = {
                Text(
                    "Update Confirmed",
                    fontWeight = FontWeight.Bold,
                    color      = GramaDarkText
                )
            },
            navigationIcon = {
                IconButton(onClick = onOk) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint               = GramaDarkText
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = GramaWhite)
        )

        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated checkmark
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(GramaGreenAccent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.Check,
                    contentDescription = "Success",
                    tint               = Color.White,
                    modifier           = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text       = "Power status updated\nsuccessfully!",
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = GramaDarkText,
                textAlign  = TextAlign.Center,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Details card
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = GramaLightGray),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ConfirmRow("Zone:",       zoneName)
                    HorizontalDivider(color = Color.LightGray)
                    ConfirmRow(
                        label      = "New Status:",
                        value      = if (newStatus) "ON" else "OFF",
                        valueColor = if (newStatus) GramaGreenAccent else GramaRed
                    )
                    HorizontalDivider(color = Color.LightGray)
                    ConfirmRow("Updated By:", farmerName)
                    HorizontalDivider(color = Color.LightGray)
                    ConfirmRow("Timestamp:",  timestamp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Notification hint
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(10.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = GramaGreen.copy(alpha = 0.08f)
                )
            ) {
                Text(
                    text      = "🔔 All users in $zoneName zone will see this update immediately.",
                    fontSize  = 12.sp,
                    color     = GramaGreen,
                    modifier  = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick  = onOk,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GramaGreen)
            ) {
                Text(
                    text       = "OK",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

@Composable
private fun ConfirmRow(
    label      : String,
    value      : String,
    valueColor : Color = GramaDarkText
) {
    Row(
        modifier                = Modifier.fillMaxWidth(),
        horizontalArrangement   = Arrangement.SpaceBetween,
        verticalAlignment       = Alignment.CenterVertically
    ) {
        Text(
            text       = label,
            fontSize   = 13.sp,
            color      = GramaGray,
            fontWeight = FontWeight.Medium,
            modifier   = Modifier.weight(0.4f)
        )
        Text(
            text          = value,
            fontSize      = 13.sp,
            color         = valueColor,
            fontWeight    = FontWeight.Bold,
            textAlign     = TextAlign.End,
            modifier      = Modifier.weight(0.6f)
        )
    }
}