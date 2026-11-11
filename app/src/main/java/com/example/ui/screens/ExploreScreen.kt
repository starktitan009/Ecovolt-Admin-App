package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SolarPower
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun ExploreScreen(
    state: EcoVoltState,
    solarTelemetry: SolarTelemetry,
    onSelectKiosk: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableStateOf(ExploreSubTab.KIOSKS) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBg)
    ) {
        // Sub-tab selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExploreSubTab.entries.forEach { tab ->
                FilterChip(
                    selected = selectedSubTab == tab,
                    onClick = { selectedSubTab = tab },
                    label = { Text(tab.label, fontWeight = FontWeight.SemiBold) }
                )
            }
        }
        HorizontalDivider(color = SubtleLine)

        when (selectedSubTab) {
            ExploreSubTab.KIOSKS -> KiosksSubView(state = state, onSelectKiosk = onSelectKiosk)
            ExploreSubTab.IMPACT -> ImpactSubView(state = state)
            ExploreSubTab.LEADERBOARD -> LeaderboardSubView(state = state)
            ExploreSubTab.SOLAR -> SolarSubView(telemetry = solarTelemetry)
            ExploreSubTab.ADMIN -> AdminSubView(state = state, telemetry = solarTelemetry)
        }
    }
}

@Composable
private fun KiosksSubView(
    state: EcoVoltState,
    onSelectKiosk: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Campus Kiosk Locator",
                subtitle = "Interactive campus map and live kiosk availability (Demo)."
            )
        }
        item {
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Text("Campus Map View (Demo)", style = MaterialTheme.typography.titleMedium, color = DeepInk)
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MintSurface)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Subtle campus pathways
                        drawLine(
                            color = Color(0xFFD0E5D7),
                            start = Offset(0f, size.height * 0.5f),
                            end = Offset(size.width, size.height * 0.5f),
                            strokeWidth = 8f
                        )
                        drawLine(
                            color = Color(0xFFD0E5D7),
                            start = Offset(size.width * 0.5f, 0f),
                            end = Offset(size.width * 0.5f, size.height),
                            strokeWidth = 8f
                        )
                        DemoCatalog.kiosks.forEach { k ->
                            val cx = size.width * k.mapXFraction
                            val cy = size.height * k.mapYFraction
                            drawCircle(
                                color = if (k.isOnline) ForestGreen else ErrorRed,
                                radius = if (k.id == state.selectedKioskId) 18f else 12f,
                                center = Offset(cx, cy)
                            )
                        }
                    }
                }
            }
        }
        items(DemoCatalog.kiosks.size) { idx ->
            val kiosk = DemoCatalog.kiosks[idx]
            val isSelected = kiosk.id == state.selectedKioskId
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = if (kiosk.isOnline) ForestGreen else ErrorRed
                        )
                        Column {
                            Text("${kiosk.id} · ${kiosk.name}", fontWeight = FontWeight.Bold, color = DeepInk)
                            Text(kiosk.location, style = MaterialTheme.typography.bodyMedium, color = MutedText)
                        }
                    }
                    EcoTag(
                        text = if (kiosk.isOnline) "Online" else "Offline",
                        containerColor = if (kiosk.isOnline) Color(0xFFE8F6ED) else ErrorRedBg,
                        contentColor = if (kiosk.isOnline) ForestGreen else ErrorRed
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ports: ${kiosk.availablePorts}/${kiosk.totalPorts} free · ${kiosk.distanceMeters}m away · Accepts: ${kiosk.acceptedMaterials.joinToString()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
                if (kiosk.isOnline) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { onSelectKiosk(kiosk.id) },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (isSelected) "Selected Kiosk" else "Set as Active Kiosk",
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImpactSubView(state: EcoVoltState) {
    val plasticKg = state.plasticCount * RecyclableMaterial.PLASTIC_BOTTLE.weightKg
    val aluminiumKg = state.aluminiumCount * RecyclableMaterial.ALUMINIUM_CAN.weightKg
    val paperKg = state.paperCount * RecyclableMaterial.PAPER_BUNDLE.weightKg
    val totalKg = state.greenImpactKg
    val co2Avoided = (state.plasticCount * 0.15) + (state.aluminiumCount * 0.25) + (state.paperCount * 0.09)

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Environmental Impact",
                subtitle = "Estimated sustainability metrics based on your demo recycling."
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EcoCard(modifier = Modifier.weight(1f)) {
                    Text("WASTE DIVERTED", style = MaterialTheme.typography.labelMedium, color = MutedText)
                    Text(String.format(Locale.US, "%.2f kg", totalKg), style = MaterialTheme.typography.headlineMedium, color = ForestGreen)
                    Text("From campus landfills", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                }
                EcoCard(modifier = Modifier.weight(1f)) {
                    Text("CO₂ AVOIDED (EST.)", style = MaterialTheme.typography.labelMedium, color = MutedText)
                    Text(String.format(Locale.US, "%.2f kg", co2Avoided), style = MaterialTheme.typography.headlineMedium, color = ForestGreen)
                    Text("Illustrative estimate", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                }
            }
        }
        item {
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Text("Material Breakdown (kg)", style = MaterialTheme.typography.titleLarge, color = DeepInk)
                Spacer(modifier = Modifier.height(12.dp))
                SimpleBarChart(
                    values = listOf(plasticKg.toFloat(), aluminiumKg.toFloat(), paperKg.toFloat()),
                    labels = listOf(
                        "Plastic (${state.plasticCount})",
                        "Aluminium (${state.aluminiumCount})",
                        "Paper (${state.paperCount})"
                    )
                )
            }
        }
        item {
            DemoNoticeBanner(
                text = "Conversion Factors (Demo): Plastic = 0.05 kg (0.15 kg CO₂e), Aluminium = 0.03 kg (0.25 kg CO₂e), Paper = 0.10 kg (0.09 kg CO₂e). These are illustrative estimates."
            )
        }
    }
}

@Composable
private fun LeaderboardSubView(state: EcoVoltState) {
    var isWeekly by remember { mutableStateOf(true) }
    val entries = listOf(
        LeaderboardEntry(1, "Aarav Sharma", "EV-USER-0102", 420, 1480, 38),
        LeaderboardEntry(2, "Priya Nair", "EV-USER-0189", 360, 1290, 31),
        LeaderboardEntry(3, "Demo User (You)", state.userId, state.ecoPoints, state.pointsEarnedTotal, state.itemsRecycled, isCurrentUser = true),
        LeaderboardEntry(4, "Rohan Verma", "EV-USER-0311", 210, 890, 19),
        LeaderboardEntry(5, "Sneha Patel", "EV-USER-0405", 175, 720, 15)
    )
    val badges = listOf(
        AchievementBadge("b1", "First Recycle", "Deposit your first item", state.itemsRecycled >= 1, "Unlocked"),
        AchievementBadge("b2", "Green Starter", "Recycle 10+ items", state.itemsRecycled >= 10, "${state.itemsRecycled}/10"),
        AchievementBadge("b3", "Plastic Fighter", "Recycle 5+ plastic bottles", state.plasticCount >= 5, "${state.plasticCount}/5"),
        AchievementBadge("b4", "Eco Champion", "Reach 300 EcoPoints", state.ecoPoints >= 300, "${state.ecoPoints}/300"),
        AchievementBadge("b5", "Sustainability Hero", "Divert 5.0 kg waste", state.greenImpactKg >= 5.0, String.format(Locale.US, "%.1f/5.0 kg", state.greenImpactKg))
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Campus Leaderboard",
                subtitle = "Top campus recyclers and sustainability badges."
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = isWeekly, onClick = { isWeekly = true }, label = { Text("Weekly") })
                FilterChip(selected = !isWeekly, onClick = { isWeekly = false }, label = { Text("Monthly") })
            }
        }
        item {
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                entries.forEachIndexed { idx, entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (entry.isCurrentUser) MintSurface else Color.Transparent, RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(if (entry.rank <= 3) ForestGreen else SubtleLine),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#${entry.rank}",
                                    color = if (entry.rank <= 3) Color.White else DeepInk,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                            Column {
                                Text(entry.name, fontWeight = FontWeight.Bold, color = DeepInk)
                                Text("${entry.itemsRecycled} items recycled", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                            }
                        }
                        Text(
                            text = "${if (isWeekly) entry.weeklyPoints else entry.monthlyPoints} pts",
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    }
                    if (idx < entries.lastIndex) HorizontalDivider(color = SubtleLine)
                }
            }
        }
        item {
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Text("Achievement Badges", style = MaterialTheme.typography.titleLarge, color = DeepInk)
                Spacer(modifier = Modifier.height(10.dp))
                badges.forEach { badge ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = null,
                                tint = if (badge.unlocked) ForestGreen else MutedText
                            )
                            Column {
                                Text(badge.title, fontWeight = FontWeight.SemiBold, color = DeepInk)
                                Text(badge.description, style = MaterialTheme.typography.bodyMedium, color = MutedText)
                            }
                        }
                        EcoTag(
                            text = if (badge.unlocked) "Unlocked" else badge.progressLabel,
                            containerColor = if (badge.unlocked) Color(0xFFE8F6ED) else Color(0xFFF1F3F2),
                            contentColor = if (badge.unlocked) ForestGreen else MutedText
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SolarSubView(telemetry: SolarTelemetry) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Solar & Energy Monitor",
                    subtitle = telemetry.kioskStatus
                )
                EcoTag("DEMO")
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EcoCard(modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.SolarPower, contentDescription = null, tint = ForestGreen)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("SOLAR POWER", style = MaterialTheme.typography.labelMedium, color = MutedText)
                    Text("${telemetry.solarPowerWatts} W", style = MaterialTheme.typography.headlineMedium, color = ForestGreen)
                    Text("${telemetry.dailyEnergyKwh} kWh today", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                }
                EcoCard(modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.BatteryChargingFull, contentDescription = null, tint = ElectricGreen)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("BATTERY", style = MaterialTheme.typography.labelMedium, color = MutedText)
                    Text("${telemetry.batteryPercent}%", style = MaterialTheme.typography.headlineMedium, color = ForestGreen)
                    Text("${telemetry.batteryVoltage} V · ${telemetry.availableStoredWh} Wh", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                }
            }
        }
        item {
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Text("Solar Generation (Watts · Today)", style = MaterialTheme.typography.titleMedium, color = DeepInk)
                Spacer(modifier = Modifier.height(10.dp))
                SimpleBarChart(
                    values = telemetry.hourlyGenerationWatts.map { it.toFloat() },
                    labels = listOf("8a", "9a", "10a", "11a", "12p", "1p", "2p", "3p", "4p", "5p"),
                    barColor = ForestGreen
                )
            }
        }
        item {
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Text("Charging Consumption (Watts · Today)", style = MaterialTheme.typography.titleMedium, color = DeepInk)
                Spacer(modifier = Modifier.height(10.dp))
                SimpleBarChart(
                    values = telemetry.hourlyConsumptionWatts.map { it.toFloat() },
                    labels = listOf("8a", "9a", "10a", "11a", "12p", "1p", "2p", "3p", "4p", "5p"),
                    barColor = Color(0xFFD97706)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Total consumed by charging sessions today: ${telemetry.chargingConsumedWh} Wh",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
            }
        }
    }
}

@Composable
private fun AdminSubView(
    state: EcoVoltState,
    telemetry: SolarTelemetry
) {
    val activeKiosks = DemoCatalog.kiosks.count { it.isOnline }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Admin Dashboard",
                    subtitle = "Campus-wide EcoVolt fleet overview."
                )
                EcoTag("DEMO ADMIN")
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EcoCard(modifier = Modifier.weight(1f)) {
                    Text("ACTIVE KIOSKS", style = MaterialTheme.typography.labelMedium, color = MutedText)
                    Text("$activeKiosks / ${DemoCatalog.kiosks.size}", style = MaterialTheme.typography.headlineMedium, color = ForestGreen)
                    Text("1 offline alert", style = MaterialTheme.typography.bodyMedium, color = ErrorRed)
                }
                EcoCard(modifier = Modifier.weight(1f)) {
                    Text("EST. REVENUE", style = MaterialTheme.typography.labelMedium, color = MutedText)
                    Text("₹1,420", style = MaterialTheme.typography.headlineMedium, color = ForestGreen)
                    Text("${state.chargingSessionsCount} sessions", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                }
            }
        }
        item {
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Text("Fleet Metrics (Demo)", style = MaterialTheme.typography.titleLarge, color = DeepInk)
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Total Materials Collected: ${String.format(Locale.US, "%.2f kg", state.greenImpactKg + 42.5)}", style = MaterialTheme.typography.bodyMedium, color = DeepInk)
                Text("• Total EcoPoints Distributed: ${state.pointsEarnedTotal + 8400} pts", style = MaterialTheme.typography.bodyMedium, color = DeepInk)
                Text("• Fleet Solar Generated Today: ${telemetry.dailyEnergyKwh * 3} kWh", style = MaterialTheme.typography.bodyMedium, color = DeepInk)
                Text("• Avg. Kiosk Battery Level: 70%", style = MaterialTheme.typography.bodyMedium, color = DeepInk)
            }
        }
        item {
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = ErrorRed)
                    Text("Kiosk Faults & Alerts", style = MaterialTheme.typography.titleMedium, color = DeepInk)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "EV-KIOSK-004 (Sports Complex): Low battery (28%) · Maintenance scheduled.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ErrorRed
                )
            }
        }
    }
}
