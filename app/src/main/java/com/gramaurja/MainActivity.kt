package com.gramaurja

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gramaurja.data.PowerRepository
import com.gramaurja.ui.screens.HomeScreen
import com.gramaurja.ui.screens.NameEntryScreen
import com.gramaurja.ui.screens.PumpTimerScreen
import com.gramaurja.ui.screens.SplashScreen
import com.gramaurja.ui.screens.UpdateConfirmationScreen
import com.gramaurja.ui.screens.ZoneSelectionScreen
import com.gramaurja.ui.theme.GramaUrjaTheme
import com.gramaurja.viewmodel.GramaUrjaViewModel

sealed class Screen {
    object Splash        : Screen()
    object NameEntry     : Screen()
    object ZoneSelection : Screen()
    object Home          : Screen()
    object Confirmation  : Screen()
    object PumpTimer     : Screen()
}

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            GramaUrjaTheme {
                GramaUrjaApp()
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GramaUrjaApp(viewModel: GramaUrjaViewModel = viewModel()) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        PowerRepository.startListening(context)
    }

    val startScreen = if (viewModel.isRegistered) Screen.Splash else Screen.NameEntry
    var currentScreen by remember { mutableStateOf<Screen>(startScreen) }

    val homeState         by viewModel.homeUiState.collectAsStateWithLifecycle()
    val showConfirmation  by viewModel.showConfirmation.collectAsStateWithLifecycle()
    val confirmedStatus   by viewModel.confirmedStatus.collectAsStateWithLifecycle()
    val confirmedZoneName by viewModel.confirmedZoneName.collectAsStateWithLifecycle()
    val farmerName        by viewModel.farmerName.collectAsStateWithLifecycle()
    val zones             by viewModel.zones.collectAsStateWithLifecycle()
    val isLoadingZones    by viewModel.isLoadingZones.collectAsStateWithLifecycle()

    LaunchedEffect(showConfirmation) {
        if (showConfirmation) currentScreen = Screen.Confirmation
    }

    val backDispatcher =
        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    DisposableEffect(currentScreen) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentScreen = when (currentScreen) {
                    Screen.ZoneSelection -> {
                        if (homeState.selectedZone != null) Screen.Home
                        else currentScreen
                    }
                    Screen.Confirmation  -> Screen.Home
                    Screen.PumpTimer     -> Screen.Home
                    Screen.Home          -> Screen.ZoneSelection
                    else                 -> currentScreen
                }
            }
        }
        backDispatcher?.addCallback(callback)
        onDispose { callback.remove() }
    }

    AnimatedContent(
        targetState  = currentScreen,
        transitionSpec = {
            slideInHorizontally(tween(350)) { it } togetherWith
                    slideOutHorizontally(tween(350)) { -it }
        },
        modifier = Modifier.fillMaxSize(),
        label    = "screen"
    ) { screen ->

        when (screen) {

            Screen.NameEntry -> NameEntryScreen(
                onNameSaved = { name ->
                    viewModel.saveFarmerName(name)
                    currentScreen = Screen.ZoneSelection
                }
            )

            Screen.Splash -> SplashScreen(
                onFinished = { currentScreen = Screen.ZoneSelection }
            )

            Screen.ZoneSelection -> ZoneSelectionScreen(
                zones          = zones,
                isLoading      = isLoadingZones,
                onZoneSelected = { zone ->
                    viewModel.selectZone(zone)
                    PowerRepository.subscribeToZone(zone.id)
                    currentScreen = Screen.Home
                },
                onBack = {
                    if (homeState.selectedZone != null) {
                        currentScreen = Screen.Home
                    }
                }
            )

            Screen.Home -> {
                val zone   = homeState.selectedZone
                val status = homeState.powerStatus
                if (zone != null && status != null) {
                    HomeScreen(
                        selectedZone = zone,
                        powerStatus  = status,
                        farmerName   = farmerName,
                        onPowerOn    = { viewModel.updatePowerStatus(true) },
                        onPowerOff   = { viewModel.updatePowerStatus(false) },
                        onSelectZone = { currentScreen = Screen.ZoneSelection },
                        onPumpTimer  = { currentScreen = Screen.PumpTimer }
                    )
                }
            }

            Screen.Confirmation -> UpdateConfirmationScreen(
                zoneName   = confirmedZoneName,
                newStatus  = confirmedStatus ?: false,
                farmerName = farmerName,
                onOk = {
                    viewModel.dismissConfirmation()
                    currentScreen = Screen.Home
                }
            )

            Screen.PumpTimer -> PumpTimerScreen(
                onBack = { currentScreen = Screen.Home }
            )
        }
    }
}