package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ChargeScreen(
    state: EcoVoltState,
    onStartCharging: (ChargingPort, ChargingPackage, Boolean) -> Unit,
    onStopCharging: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPort by remember { mutableStateOf(DemoCatalog.chargingPorts.first()) }
    var selectedPackage by remember { mutableStateOf(DemoCatalog.chargingPackages.first()) }
    var useEcoPoints by remember { mutableStateOf(true) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val activeSession = state.activeCharging

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "Mobile Phone Charging",
                subtitle = "Select a port, choose a package, and start a simulated session."
            )
        }

        // Active Session Card
        if (activeSession != null) {
            item {
                val mins = activeSession.remainingSeconds / 60
                val secs = activeSession.remainingSeconds % 60
                val progress = activeSession.remainingSeconds.toFloat() / activeSession.totalSeconds.coerceAtLeast(1)

                EcoCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Active Charging Session",
                            style = MaterialTheme.typography.titleLarge,
                            color = ForestGreen
                        )
                        EcoTag("DEMO ACTIVE")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = String.format("%02d:%02d", mins, secs),
                        style = MaterialTheme.typography.headlineLarge,
                        color = DeepInk
                    )
                    Text(
                        text = "${activeSession.portName} · ${activeSession.packageLabel} (${activeSession.paymentMethod})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = ElectricGreen,
                        trackColor = MintSurface
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onStopCharging,
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("stop_charging_button")
                    ) {
                        Text("End Charging Session", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Port Selection
        item {
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "1. Select Charging Port",
                    style = MaterialTheme.typography.titleLarge,
                    color = DeepInk
                )
                Spacer(modifier = Modifier.height(10.dp))
                DemoCatalog.chargingPorts.forEach { port ->
                    val isOccupied = activeSession?.portId == port.id
                    val isSelected = selectedPort.id == port.id
                    Surface(
                        color = if (isSelected) MintSurface else Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) ForestGreen else SubtleLine
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable(enabled = activeSession == null) { selectedPort = port }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Usb,
                                    contentDescription = port.name,
                                    tint = ForestGreen
                                )
                                Column {
                                    Text("${port.name} — ${port.connectorType}", fontWeight = FontWeight.SemiBold, color = DeepInk)
                                    Text("Kiosk: ${state.selectedKioskId}", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                                }
                            }
                            EcoTag(
                                text = if (isOccupied) "Occupied" else "Available",
                                containerColor = if (isOccupied) ErrorRedBg else Color(0xFFE8F6ED),
                                contentColor = if (isOccupied) ErrorRed else ForestGreen
                            )
                        }
                    }
                }
            }
        }

        // Package & Payment Selection
        item {
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "2. Select Charging Package",
                    style = MaterialTheme.typography.titleLarge,
                    color = DeepInk
                )
                Spacer(modifier = Modifier.height(10.dp))
                DemoCatalog.chargingPackages.forEach { pkg ->
                    val isSelected = selectedPackage.id == pkg.id
                    Surface(
                        color = if (isSelected) MintSurface else Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) ForestGreen else SubtleLine
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable(enabled = activeSession == null) { selectedPackage = pkg }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(pkg.label, fontWeight = FontWeight.SemiBold, color = DeepInk)
                                Text("${pkg.durationMinutes} minutes", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                            }
                            Text(
                                text = "${pkg.pointsCost} pts / ₹${pkg.demoPriceInr}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = useEcoPoints,
                        onClick = { useEcoPoints = true },
                        label = { Text("Use EcoPoints (${selectedPackage.pointsCost} pts)") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = !useEcoPoints,
                        onClick = { useEcoPoints = false },
                        label = { Text("Demo Pay (₹${selectedPackage.demoPriceInr})") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { showConfirmDialog = true },
                    enabled = activeSession == null,
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("start_charging_button")
                ) {
                    Icon(Icons.Filled.Bolt, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (activeSession == null) "Start Demo Charging" else "Session In Progress",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Charging History
        item {
            val chargeTxns = state.transactions.filter { it.type == TransactionType.CHARGING }
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Charging Session History",
                    style = MaterialTheme.typography.titleLarge,
                    color = DeepInk
                )
                Spacer(modifier = Modifier.height(6.dp))
                if (chargeTxns.isEmpty()) {
                    Text(
                        text = "No charging sessions recorded yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText
                    )
                } else {
                    chargeTxns.take(5).forEachIndexed { idx, tx ->
                        TransactionRow(tx = tx)
                        if (idx < 4 && idx < chargeTxns.lastIndex) {
                            HorizontalDivider(color = SubtleLine)
                        }
                    }
                }
            }
        }

        item {
            DemoNoticeBanner(
                text = "Simulated Charging: This prototype simulates port control and countdown timers. It does not physically supply electricity."
            )
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Charging Session", fontWeight = FontWeight.Bold) },
            text = {
                val paymentText = if (useEcoPoints) {
                    "${selectedPackage.pointsCost} EcoPoints"
                } else {
                    "₹${selectedPackage.demoPriceInr} Demo Payment"
                }
                Text("Start ${selectedPackage.label} on ${selectedPort.name} (${selectedPort.connectorType}) using $paymentText?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onStartCharging(selectedPort, selectedPackage, useEcoPoints)
                        showConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text("Start Session")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel", color = MutedText)
                }
            }
        )
    }
}
