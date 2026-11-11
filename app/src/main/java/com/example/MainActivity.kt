package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.NavTab
import com.example.ui.EcoVoltViewModel
import com.example.ui.components.EcoVoltTopBar
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcoVoltTheme {
                EcoVoltApp()
            }
        }
    }
}

@Composable
fun EcoVoltApp(
    viewModel: EcoVoltViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isVerifying by viewModel.isVerifyingDeposit.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf(NavTab.HOME) }
    var showResetDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        containerColor = OffWhiteBg,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            EcoVoltTopBar(
                ecoPoints = state.ecoPoints,
                onResetClick = { showResetDialog = true }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = ForestGreen
            ) {
                NavTab.entries.forEach { tab ->
                    val icon = when (tab) {
                        NavTab.HOME -> Icons.Filled.Home
                        NavTab.RECYCLE -> Icons.Filled.Recycling
                        NavTab.REWARDS -> Icons.Filled.CardGiftcard
                        NavTab.CHARGE -> Icons.Filled.Bolt
                        NavTab.EXPLORE -> Icons.Filled.Explore
                    }
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = { Icon(icon, contentDescription = tab.label) },
                        label = { Text(tab.label, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ForestGreen,
                            selectedTextColor = ForestGreen,
                            indicatorColor = MintSurface,
                            unselectedIconColor = MutedText,
                            unselectedTextColor = MutedText
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentTab) {
                NavTab.HOME -> HomeScreen(
                    state = state,
                    onNavigate = { currentTab = it }
                )
                NavTab.RECYCLE -> RecycleScreen(
                    state = state,
                    isVerifying = isVerifying,
                    onSelectKiosk = viewModel::selectKiosk,
                    onDeposit = viewModel::depositRecyclable,
                    onRedeemDepositQr = viewModel::redeemDepositQr
                )
                NavTab.REWARDS -> RewardsScreen(
                    state = state,
                    onRedeemCoupon = viewModel::redeemCoupon
                )
                NavTab.CHARGE -> ChargeScreen(
                    state = state,
                    onStartCharging = viewModel::startChargingSession,
                    onStopCharging = { viewModel.stopChargingSession(expired = false) }
                )
                NavTab.EXPLORE -> ExploreScreen(
                    state = state,
                    solarTelemetry = viewModel.getSolarTelemetry(),
                    onSelectKiosk = viewModel::selectKiosk
                )
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Demo Data?", fontWeight = FontWeight.Bold) },
            text = {
                Text("This will restore your EcoPoints (250), items recycled (12), rewards claimed (2), and clear demo history.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetDemo()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = MutedText)
                }
            }
        )
    }
}
