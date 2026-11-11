package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DemoCatalog
import com.example.data.EcoVoltState
import com.example.data.NavTab
import com.example.ui.components.*
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun HomeScreen(
    state: EcoVoltState,
    onNavigate: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(ForestGreenDark, ElectricGreen)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "A SMARTER WAY TO GO GREEN",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFC7EAD5),
                        letterSpacing = 1.6.sp
                    )
                    Text(
                        text = "Small actions.\nGreener campuses.",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                    Text(
                        text = "Recycle everyday waste, earn EcoPoints and conveniently charge your mobile phone — all through one connected kiosk experience.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE0F2E6)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { onNavigate(NavTab.RECYCLE) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = ForestGreen
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("hero_start_recycling_button")
                    ) {
                        Text("Start recycling", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 4 KPI Cards (2x2 grid)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiCard(
                        label = "YOUR ECOPOINTS",
                        value = "${state.ecoPoints}",
                        subtitle = "Available to redeem",
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        label = "ITEMS RECYCLED",
                        value = "${state.itemsRecycled}",
                        subtitle = "Demo account activity",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiCard(
                        label = "REWARDS CLAIMED",
                        value = "${state.rewardsClaimed}",
                        subtitle = "Sample redemptions",
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        label = "GREEN IMPACT",
                        value = String.format(Locale.US, "%.2f kg", state.greenImpactKg),
                        subtitle = "Material collected",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Sustainability Summary
        item {
            val estimatedCo2 = state.greenImpactKg * 2.4
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sustainability Summary",
                        style = MaterialTheme.typography.titleLarge,
                        color = DeepInk
                    )
                    EcoTag("DEMO ESTIMATE")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You've diverted ${String.format(Locale.US, "%.2f kg", state.greenImpactKg)} of recyclables (${state.plasticCount} plastic, ${state.aluminiumCount} aluminium, ${state.paperCount} paper), avoiding an estimated ${String.format(Locale.US, "%.2f kg", estimatedCo2)} CO₂e.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
            }
        }

        // Quick Actions
        item {
            SectionHeader(
                title = "Quick Actions",
                subtitle = "Choose what you would like to do today."
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickActionCard(
                    icon = Icons.Filled.Recycling,
                    title = "Recycle & earn",
                    description = "Deposit an accepted recyclable item and collect EcoPoints.",
                    buttonLabel = "Try recycling",
                    onClick = { onNavigate(NavTab.RECYCLE) },
                    testTag = "quick_action_recycle"
                )
                QuickActionCard(
                    icon = Icons.Filled.CardGiftcard,
                    title = "Explore coupons",
                    description = "Use your points to unlock sample campus offers.",
                    buttonLabel = "View rewards",
                    onClick = { onNavigate(NavTab.REWARDS) },
                    testTag = "quick_action_rewards"
                )
                QuickActionCard(
                    icon = Icons.Filled.Bolt,
                    title = "Phone charging",
                    description = "Start a simulated solar-powered mobile charging session.",
                    buttonLabel = "See charging",
                    onClick = { onNavigate(NavTab.CHARGE) },
                    testTag = "quick_action_charge"
                )
            }
        }

        // Nearby Kiosk Availability
        item {
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nearby Kiosks",
                        style = MaterialTheme.typography.titleLarge,
                        color = DeepInk
                    )
                    TextButton(onClick = { onNavigate(NavTab.EXPLORE) }) {
                        Text("Explore all", color = ForestGreen, fontWeight = FontWeight.Bold)
                    }
                }
                DemoCatalog.kiosks.take(2).forEach { kiosk ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = ForestGreen
                            )
                            Column {
                                Text(
                                    text = "${kiosk.id} · ${kiosk.name}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DeepInk
                                )
                                Text(
                                    text = "${kiosk.distanceMeters}m away · ${kiosk.availablePorts}/${kiosk.totalPorts} ports free",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MutedText
                                )
                            }
                        }
                        EcoTag(if (kiosk.isOnline) "Online" else "Offline")
                    }
                }
            }
        }

        // Recent Transactions
        item {
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Activity",
                        style = MaterialTheme.typography.titleLarge,
                        color = DeepInk
                    )
                    TextButton(onClick = { onNavigate(NavTab.REWARDS) }) {
                        Text("View all", color = ForestGreen, fontWeight = FontWeight.Bold)
                    }
                }
                state.transactions.take(3).forEachIndexed { idx, tx ->
                    TransactionRow(tx = tx)
                    if (idx < 2 && idx < state.transactions.lastIndex) {
                        HorizontalDivider(color = SubtleLine)
                    }
                }
            }
        }

        // Prototype Notice
        item {
            DemoNoticeBanner(
                text = "Prototype demonstration only: points, coupons, QR codes and kiosk actions are sample data. No real recycling machine, charging hardware or coupon partner is connected."
            )
        }
    }
}

@Composable
private fun KpiCard(
    label: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    EcoCard(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MutedText
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = ForestGreen,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MutedText
        )
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    title: String,
    description: String,
    buttonLabel: String,
    onClick: () -> Unit,
    testTag: String
) {
    EcoCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(MintSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = ForestGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepInk
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
            }
            OutlinedButton(
                onClick = onClick,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(buttonLabel, color = ForestGreen, fontWeight = FontWeight.Bold)
            }
        }
    }
}
