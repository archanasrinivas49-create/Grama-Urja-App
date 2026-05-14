package com.gramaurja.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gramaurja.data.Zone
import com.gramaurja.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneSelectionScreen(
    zones          : List<Zone>,
    isLoading      : Boolean = false,
    onZoneSelected : (Zone) -> Unit,
    onBack         : () -> Unit
) {
    var selectedDropdown by remember(zones) {
        mutableStateOf(zones.firstOrNull())
    }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GramaWhite)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text       = "Select Zone",
                    fontWeight = FontWeight.Bold,
                    color      = GramaDarkText
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector        = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint               = GramaDarkText
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = GramaWhite
            )
        )

        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

        // ── Loading state ─────────────────────────────────────────────────────
        if (isLoading) {
            Box(
                modifier         = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = GramaGreen)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text     = "Loading zones...",
                        fontSize = 14.sp,
                        color    = GramaGray
                    )
                }
            }
            return@Column
        }

        // ── Empty state ───────────────────────────────────────────────────────
        if (zones.isEmpty()) {
            Box(
                modifier         = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier            = Modifier.padding(32.dp)
                ) {
                    Text(text = "🏘️", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text       = "No zones found",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color      = GramaDarkText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text      = "Add zones in Firebase Console\nunder the zones_list node",
                        fontSize  = 13.sp,
                        color     = GramaGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
            return@Column
        }

        // ── Normal state ──────────────────────────────────────────────────────
        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                text       = "Choose your Village /\nTransformer Zone",
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                color      = GramaDarkText,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown
            Box {
                OutlinedTextField(
                    value         = selectedDropdown?.name ?: "",
                    onValueChange = {},
                    readOnly      = true,
                    modifier      = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
                    trailingIcon  = {
                        Icon(
                            imageVector        = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand",
                            tint               = GramaGreen,
                            modifier           = Modifier.clickable { expanded = true }
                        )
                    },
                    shape  = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = GramaGreen,
                        unfocusedBorderColor = Color.LightGray,
                        focusedTextColor     = GramaDarkText,
                        unfocusedTextColor   = GramaDarkText
                    )
                )

                DropdownMenu(
                    expanded         = expanded,
                    onDismissRequest = { expanded = false },
                    modifier         = Modifier
                        .fillMaxWidth(0.9f)
                        .background(GramaWhite)
                ) {
                    zones.forEach { zone ->
                        DropdownMenuItem(
                            text    = { Text(zone.name, color = GramaDarkText) },
                            onClick = {
                                selectedDropdown = zone
                                expanded         = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Zone count badge
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text     = "Available Zones",
                    fontSize = 13.sp,
                    color    = GramaGray
                )
                Box(
                    modifier = Modifier
                        .background(GramaGreen, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text       = "${zones.size} zones",
                        fontSize   = 11.sp,
                        color      = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Zone list
            LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                items(zones) { zone ->
                    ZoneListItem(
                        zone       = zone,
                        isSelected = zone.id == selectedDropdown?.id,
                        onClick    = {
                            selectedDropdown = zone
                            onZoneSelected(zone)
                        }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
                }
            }
        }
    }
}

@Composable
private fun ZoneListItem(
    zone       : Zone,
    isSelected : Boolean,
    onClick    : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (isSelected) GramaGreen.copy(alpha = 0.06f)
                else Color.Transparent
            )
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector        = Icons.Default.LocationOn,
            contentDescription = null,
            tint               = GramaGreen,
            modifier           = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = zone.name,
                fontSize   = 16.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color      = if (isSelected) GramaGreen else GramaDarkText
            )
            Text(
                text     = zone.transformerZone,
                fontSize = 12.sp,
                color    = GramaGray
            )
        }
        if (isSelected) {
            Text(
                text       = "✓",
                color      = GramaGreen,
                fontWeight = FontWeight.Bold
            )
        }
    }
}