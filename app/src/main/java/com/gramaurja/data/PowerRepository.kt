package com.gramaurja.data

import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

data class Zone(
    val id: String,
    val name: String,
    val transformerZone: String = name
)

data class PowerStatus(
    val isOn: Boolean,
    val lastUpdated: Long = System.currentTimeMillis(),
    val updatedBy: String = "Community"
) {
    fun lastSeenText(): String {
        val diff = System.currentTimeMillis() - lastUpdated
        val minutes = diff / 60000
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val timeStr = sdf.format(Date(lastUpdated))
        return when {
            minutes < 1   -> "Just now ($timeStr)"
            minutes == 1L -> "1 min ago ($timeStr)"
            minutes < 60  -> "$minutes mins ago ($timeStr)"
            else          -> sdf.format(Date(lastUpdated))
        }
    }

    fun freshnessLabel(): String {
        val minutes = (System.currentTimeMillis() - lastUpdated) / 60000
        return when {
            minutes < 5  -> "Fresh"
            minutes < 30 -> "Recent"
            minutes < 60 -> "Old"
            else         -> "Stale"
        }
    }

    fun freshnessColor(): androidx.compose.ui.graphics.Color {
        val minutes = (System.currentTimeMillis() - lastUpdated) / 60000
        return when {
            minutes < 5  -> androidx.compose.ui.graphics.Color(0xFF2E7D32)
            minutes < 30 -> androidx.compose.ui.graphics.Color(0xFFF57F17)
            else         -> androidx.compose.ui.graphics.Color(0xFFC62828)
        }
    }
}

object PowerRepository {

    // Replace with your actual Firebase URL
    private val database by lazy {
        val db = FirebaseDatabase.getInstance(
            "https://gramaurja-927ae-default-rtdb.asia-southeast1.firebasedatabase.app"
        )
        db.setPersistenceEnabled(true)
        db
    }

    private val _zones = MutableStateFlow<List<Zone>>(emptyList())
    val zones: StateFlow<List<Zone>> = _zones

    private val _isLoadingZones = MutableStateFlow(true)
    val isLoadingZones: StateFlow<Boolean> = _isLoadingZones

    private val _statusMap = MutableStateFlow<Map<String, PowerStatus>>(emptyMap())
    val statusMap: StateFlow<Map<String, PowerStatus>> = _statusMap

    private var appContext: Context? = null

    fun startListening(context: Context) {
        appContext = context.applicationContext
        NotificationHelper.createNotificationChannel(context)
        loadZonesFromFirebase()
    }

    private fun loadZonesFromFirebase() {
        val ref = database.getReference("zones_list")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val zoneList = mutableListOf<Zone>()
                for (child in snapshot.children) {
                    val id          = child.key ?: continue
                    val name        = child.child("name")
                        .getValue(String::class.java) ?: continue
                    val transformer = child.child("transformer")
                        .getValue(String::class.java) ?: name
                    zoneList.add(
                        Zone(
                            id              = id,
                            name            = name,
                            transformerZone = transformer
                        )
                    )
                }
                _zones.value          = zoneList
                _isLoadingZones.value = false
                listenToPowerStatus(zoneList)
            }

            override fun onCancelled(error: DatabaseError) {
                _isLoadingZones.value = false
            }
        })
    }

    private fun listenToPowerStatus(zoneList: List<Zone>) {
        zoneList.forEach { zone ->
            val ref = database.getReference("zones/${zone.id}/status")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isOn      = snapshot.child("isOn")
                        .getValue(Boolean::class.java) ?: false
                    val ts        = snapshot.child("timestamp")
                        .getValue(Long::class.java)
                        ?: System.currentTimeMillis()
                    val updatedBy = snapshot.child("updatedBy")
                        .getValue(String::class.java) ?: "Community"

                    val current        = _statusMap.value.toMutableMap()
                    val previousStatus = current[zone.id]
                    val newStatus      = PowerStatus(
                        isOn        = isOn,
                        lastUpdated = ts,
                        updatedBy   = updatedBy
                    )
                    current[zone.id]  = newStatus
                    _statusMap.value  = current

                    // Show notification only when status changes
                    val ctx = appContext
                    if (ctx != null && previousStatus != null &&
                        previousStatus.isOn != isOn
                    ) {
                        val emoji   = if (isOn) "⚡" else "🔴"
                        val title   = "$emoji Power ${if (isOn) "ON" else "OFF"} — ${zone.name}"
                        val message = if (isOn) {
                            "Electricity is now available in ${zone.name}.\nUpdated by $updatedBy"
                        } else {
                            "Power has gone OFF in ${zone.name}.\nUpdated by $updatedBy"
                        }
                        NotificationHelper.showLocalNotification(
                            context = ctx,
                            title   = title,
                            message = message
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    fun subscribeToZone(zoneId: String) {
        NotificationHelper.subscribeToZone(zoneId)
    }

    fun unsubscribeFromZone(zoneId: String) {
        NotificationHelper.unsubscribeFromZone(zoneId)
    }

    fun updateStatus(
        zoneId    : String,
        isOn      : Boolean,
        updatedBy : String = "Farmer"
    ) {
        database.getReference("zones/$zoneId/status").setValue(
            mapOf(
                "isOn"      to isOn,
                "timestamp" to System.currentTimeMillis(),
                "updatedBy" to updatedBy
            )
        )
        val current = _statusMap.value.toMutableMap()
        current[zoneId] = PowerStatus(
            isOn        = isOn,
            lastUpdated = System.currentTimeMillis(),
            updatedBy   = updatedBy
        )
        _statusMap.value = current
    }

    fun getStatus(zoneId: String): PowerStatus =
        _statusMap.value[zoneId] ?: PowerStatus(isOn = false)
}