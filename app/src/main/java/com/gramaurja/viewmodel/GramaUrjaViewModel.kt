package com.gramaurja.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gramaurja.data.PowerRepository
import com.gramaurja.data.PowerStatus
import com.gramaurja.data.UserPreferences
import com.gramaurja.data.Zone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedZone     : Zone?        = null,
    val powerStatus      : PowerStatus? = null
)

class GramaUrjaViewModel(application: Application) : AndroidViewModel(application) {

    private val context get() = getApplication<Application>()

    val zones          : StateFlow<List<Zone>> = PowerRepository.zones
    val isLoadingZones : StateFlow<Boolean>    = PowerRepository.isLoadingZones

    private val _farmerName = MutableStateFlow(
        UserPreferences.getFarmerName(context)
    )
    val farmerName: StateFlow<String> = _farmerName

    val isRegistered: Boolean
        get() = UserPreferences.isRegistered(context)

    fun saveFarmerName(name: String) {
        UserPreferences.saveFarmerName(context, name)
        _farmerName.value = name
    }

    private val _selectedZone = MutableStateFlow<Zone?>(null)

    val homeUiState: StateFlow<HomeUiState> = combine(
        _selectedZone,
        PowerRepository.statusMap
    ) { zone, statusMap ->
        HomeUiState(
            selectedZone = zone,
            powerStatus  = zone?.let { statusMap[it.id] }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeUiState()
    )

    private val _showConfirmation  = MutableStateFlow(false)
    val showConfirmation: StateFlow<Boolean> = _showConfirmation

    private val _confirmedStatus   = MutableStateFlow<Boolean?>(null)
    val confirmedStatus: StateFlow<Boolean?> = _confirmedStatus

    private val _confirmedZoneName = MutableStateFlow("")
    val confirmedZoneName: StateFlow<String> = _confirmedZoneName

    fun selectZone(zone: Zone) {
        _selectedZone.value = zone
    }

    fun updatePowerStatus(isOn: Boolean) {
        val zone = _selectedZone.value ?: return
        viewModelScope.launch {
            PowerRepository.updateStatus(
                zoneId    = zone.id,
                isOn      = isOn,
                updatedBy = _farmerName.value
            )
            _confirmedStatus.value   = isOn
            _confirmedZoneName.value = zone.name
            _showConfirmation.value  = true
        }
    }

    fun dismissConfirmation() {
        _showConfirmation.value = false
    }
}