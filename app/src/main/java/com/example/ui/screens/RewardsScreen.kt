package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.CouponOffer
import com.example.data.DemoCatalog
import com.example.data.EcoVoltState
import com.example.data.TransactionType
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun RewardsScreen(
    state: EcoVoltState,
    onRedeemCoupon: (CouponOffer) -> Unit,
    modifier: Modifier = Modifier
) {
    var pendingCoupon by remember { mutableStateOf<CouponOffer?>(null) }
    var selectedFilter by remember { mutableStateOf<TransactionType?>(null) }

    val milestoneTarget = 400
    val progress = (state.ecoPoints.toFloat() / milestoneTarget.toFloat()).coerceIn(0f, 1f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "EcoPoints & Rewards",
                subtitle = "Your reward wallet, campus offers, and unified transactions."
            )
        }

        // Wallet Card
        item {
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AVAILABLE BALANCE",
                            style = MaterialTheme.typography.labelMedium,
                            color = MutedText
                        )
                        Text(
                            text = "${state.ecoPoints} pts",
                            style = MaterialTheme.typography.headlineLarge,
                            color = ForestGreen
                        )
                    }
                    Surface(
                        color = MintSurface,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SubtleLine)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.QrCode2,
                                contentDescription = "Reward QR",
                                tint = ForestGreen
                            )
                            Text(
                                text = state.userId,
                                style = MaterialTheme.typography.labelMedium,
                                color = ForestGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(9.dp),
                    color = ElectricGreen,
                    trackColor = Color(0xFFE9F1EB)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Progress to next milestone · ${state.ecoPoints} / $milestoneTarget pts",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = SubtleLine)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Points Earned", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                        Text("+${state.pointsEarnedTotal} pts", style = MaterialTheme.typography.titleMedium, color = ForestGreen)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Points Redeemed", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                        Text("-${state.pointsRedeemedTotal} pts", style = MaterialTheme.typography.titleMedium, color = DeepInk)
                    }
                }
            }
        }

        // Coupons & Offers
        item {
            Text(
                text = "Coupons & Offers (Demo)",
                style = MaterialTheme.typography.titleLarge,
                color = DeepInk
            )
        }

        items(DemoCatalog.coupons.size) { index ->
            val offer = DemoCatalog.coupons[index]
            val canAfford = state.ecoPoints >= offer.costPoints
            Surface(
                color = Color(0xFFFBFDFB),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF9BC8A9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        EcoTag(offer.tag)
                        Text(
                            text = "${offer.costPoints} EcoPoints",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = offer.discount,
                        style = MaterialTheme.typography.headlineMedium,
                        color = ForestGreen,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = offer.partner,
                        style = MaterialTheme.typography.titleMedium,
                        color = DeepInk
                    )
                    Text(
                        text = offer.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xFFEDF6EF),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = offer.codePrefix,
                                fontFamily = FontFamily.Monospace,
                                color = ForestGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                        Button(
                            onClick = { pendingCoupon = offer },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canAfford) ForestGreen else MutedText
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("redeem_${offer.id}")
                        ) {
                            Text(if (canAfford) "Redeem" else "Need ${offer.costPoints} pts")
                        }
                    }
                }
            }
        }

        // Redeemed Coupons History
        item {
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "My Redeemed Coupons",
                    style = MaterialTheme.typography.titleLarge,
                    color = DeepInk
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (state.redeemedCoupons.isEmpty()) {
                    Text(
                        text = "No demo coupons redeemed yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText
                    )
                } else {
                    state.redeemedCoupons.forEachIndexed { idx, c ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocalOffer,
                                    contentDescription = null,
                                    tint = ForestGreen
                                )
                                Column {
                                    Text("${c.partner} · ${c.discount}", fontWeight = FontWeight.SemiBold, color = DeepInk)
                                    Text("Code: ${c.code} · ${c.redeemedAt}", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                                }
                            }
                            EcoTag("Redeemed")
                        }
                        if (idx < state.redeemedCoupons.lastIndex) {
                            HorizontalDivider(color = SubtleLine)
                        }
                    }
                }
            }
        }

        // Unified Transaction History
        item {
            val filtered = if (selectedFilter == null) {
                state.transactions
            } else {
                state.transactions.filter { it.type == selectedFilter }
            }
            EcoCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Unified Transaction History",
                    style = MaterialTheme.typography.titleLarge,
                    color = DeepInk
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = { Text("All") }
                    )
                    FilterChip(
                        selected = selectedFilter == TransactionType.RECYCLE,
                        onClick = { selectedFilter = TransactionType.RECYCLE },
                        label = { Text("Recycle") }
                    )
                    FilterChip(
                        selected = selectedFilter == TransactionType.COUPON,
                        onClick = { selectedFilter = TransactionType.COUPON },
                        label = { Text("Coupons") }
                    )
                    FilterChip(
                        selected = selectedFilter == TransactionType.CHARGING,
                        onClick = { selectedFilter = TransactionType.CHARGING },
                        label = { Text("Charge") }
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                if (filtered.isEmpty()) {
                    Text(
                        text = "No transactions for this filter.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText
                    )
                } else {
                    filtered.take(10).forEachIndexed { idx, tx ->
                        TransactionRow(tx = tx)
                        if (idx < filtered.take(10).lastIndex) {
                            HorizontalDivider(color = SubtleLine)
                        }
                    }
                }
            }
        }

        item {
            DemoNoticeBanner(
                text = "Demonstration Offers: Coupon names, discounts, and codes are fictional sample offers for this prototype."
            )
        }
    }

    pendingCoupon?.let { offer ->
        AlertDialog(
            onDismissRequest = { pendingCoupon = null },
            title = { Text("Confirm Redemption", fontWeight = FontWeight.Bold) },
            text = {
                Text("Redeem ${offer.partner} (${offer.discount}) for ${offer.costPoints} EcoPoints?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRedeemCoupon(offer)
                        pendingCoupon = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingCoupon = null }) {
                    Text("Cancel", color = MutedText)
                }
            }
        )
    }
}
