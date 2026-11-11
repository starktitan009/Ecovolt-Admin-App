package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.DemoCatalog
import com.example.data.EcoVoltState
import com.example.data.RecyclableMaterial
import com.example.data.TransactionType
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlin.random.Random

@Composable
fun RecycleScreen(
    state: EcoVoltState,
    isVerifying: Boolean,
    onSelectKiosk: (String) -> Unit,
    onDeposit: (RecyclableMaterial, String) -> Unit,
    onRedeemDepositQr: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMaterial by remember { mutableStateOf(RecyclableMaterial.PLASTIC_BOTTLE) }
    var showQrDialog by remember { mutableStateOf(false) }
    var pendingToken by remember { mutableStateOf("DEP-${Random.nextInt(10000, 99999)}") }
    var depositQrPayload by remember { mutableStateOf("ECOVOLT:3_BOTTLES:CASH_15:${Random.nextInt(100, 999)}") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "Recycle & Earn EcoPoints",
                subtitle = "Simulate depositing an item into an EcoVolt kiosk."
            )
        }

        // Kiosk Identification & QR Scan
        item {
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Connected Kiosk",
                            style = MaterialTheme.typography.labelMedium,
                            color = MutedText
                        )
                        Text(
                            text = state.selectedKioskId,
                            style = MaterialTheme.typography.titleLarge,
                            color = ForestGreen
                        )
                        val kioskName = DemoCatalog.kiosks.find { it.id == state.selectedKioskId }?.name ?: "Campus Kiosk"
                        Text(
                            text = kioskName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedText
                        )
                    }
                    OutlinedButton(
                        onClick = { showQrDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("scan_kiosk_qr_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.QrCodeScanner,
                            contentDescription = "Scan Kiosk QR",
                            tint = ForestGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Scan QR", color = ForestGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Material Selection
        item {
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Choose an item to deposit",
                        style = MaterialTheme.typography.titleLarge,
                        color = DeepInk
                    )
                    EcoTag("SIMULATED")
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Select a recyclable material. Simulated sensor verification prevents duplicate crediting for the same deposit token.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
                Spacer(modifier = Modifier.height(12.dp))

                RecyclableMaterial.entries.forEach { material ->
                    val isSelected = selectedMaterial == material
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
                            .clickable { selectedMaterial = material }
                            .testTag("material_${material.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Filled.Recycling,
                                    contentDescription = material.displayName,
                                    tint = if (isSelected) ForestGreen else MutedText
                                )
                                Column {
                                    Text(
                                        text = material.displayName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DeepInk
                                    )
                                    Text(
                                        text = "Est. weight: ${material.weightKg} kg",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MutedText
                                    )
                                }
                            }
                            EcoTag("+${material.points} pts")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        onDeposit(selectedMaterial, pendingToken)
                        pendingToken = "DEP-${Random.nextInt(10000, 99999)}"
                    },
                    enabled = !isVerifying,
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("simulate_deposit_button")
                ) {
                    if (isVerifying) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Verifying with Kiosk...", fontWeight = FontWeight.Bold)
                    } else {
                        Text(
                            text = "Simulate Deposit (+${selectedMaterial.points} pts)",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Generated Deposit QR (3 Bottles + Cash)
        item {
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Generated Deposit QR",
                        style = MaterialTheme.typography.titleLarge,
                        color = DeepInk
                    )
                    EcoTag("3 BOTTLES + ₹15")
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "This demo QR encodes 3 Plastic Bottles (+60 EcoPoints) and ₹15 Demo Cash bonus.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    DemoQrCodeCanvas(payload = depositQrPayload)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Payload: $depositQrPayload",
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedText
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            depositQrPayload = "ECOVOLT:3_BOTTLES:CASH_15:${Random.nextInt(100, 999)}"
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("New QR", color = ForestGreen, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onRedeemDepositQr(depositQrPayload) },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("recognize_qr_button")
                    ) {
                        Text("Recognize QR", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Dynamic Recycling Stats
        item {
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Your Recycling Breakdown",
                    style = MaterialTheme.typography.titleLarge,
                    color = DeepInk
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${state.plasticCount}", style = MaterialTheme.typography.headlineMedium, color = ForestGreen)
                        Text("Plastic", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${state.aluminiumCount}", style = MaterialTheme.typography.headlineMedium, color = ForestGreen)
                        Text("Aluminium", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${state.paperCount}", style = MaterialTheme.typography.headlineMedium, color = ForestGreen)
                        Text("Paper", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                    }
                }
            }
        }

        // Recent Recycling Deposits
        item {
            val recycleTxns = state.transactions.filter { it.type == TransactionType.RECYCLE }
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Recent Recycling Deposits",
                    style = MaterialTheme.typography.titleLarge,
                    color = DeepInk
                )
                Spacer(modifier = Modifier.height(6.dp))
                if (recycleTxns.isEmpty()) {
                    Text(
                        text = "No recycling deposits yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText
                    )
                } else {
                    recycleTxns.take(5).forEachIndexed { idx, tx ->
                        TransactionRow(tx = tx)
                        if (idx < 4 && idx < recycleTxns.lastIndex) {
                            HorizontalDivider(color = SubtleLine)
                        }
                    }
                }
            }
        }

        item {
            DemoNoticeBanner(
                text = "Hardware Note: In production, an ESP32-connected optical/inductive sensor verifies material type before crediting EcoPoints. Here, verification is simulated."
            )
        }
    }

    if (showQrDialog) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            title = { Text("Demo Kiosk QR Scanner", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CameraPreview()
                    Text(
                        text = "Or select a demo kiosk QR code:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText
                    )
                    DemoCatalog.kiosks.filter { it.isOnline }.forEach { kiosk ->
                        OutlinedButton(
                            onClick = {
                                onSelectKiosk(kiosk.id)
                                showQrDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("${kiosk.id} — ${kiosk.name}", color = ForestGreen)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showQrDialog = false }) {
                    Text("Close", color = ForestGreen)
                }
            }
        )
    }
}
