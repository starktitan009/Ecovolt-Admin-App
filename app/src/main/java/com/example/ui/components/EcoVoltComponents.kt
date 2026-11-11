package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EcoTransaction
import com.example.data.TransactionType
import com.example.ui.theme.*

@Composable
fun EcoVoltTopBar(
    ecoPoints: Int,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Recycling,
                            contentDescription = "EcoVolt logo",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "EcoVolt",
                            style = MaterialTheme.typography.titleLarge,
                            color = ForestGreen,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Recycle • Reward • Recharge",
                            style = MaterialTheme.typography.labelMedium,
                            color = MutedText
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = MintSurface,
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, SubtleLine),
                        modifier = Modifier.testTag("header_points_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Eco,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "$ecoPoints pts",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen
                            )
                        }
                    }

                    IconButton(
                        onClick = onResetClick,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("reset_demo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Reset Demo Data",
                            tint = MutedText
                        )
                    }
                }
            }
            HorizontalDivider(color = SubtleLine, thickness = 1.dp)
        }
    }
}

@Composable
fun EcoCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, SubtleLine),
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun EcoTag(
    text: String,
    containerColor: Color = Color(0xFFE8F6ED),
    contentColor: Color = ForestGreen,
    modifier: Modifier = Modifier
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun DemoNoticeBanner(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = AmberNoticeBg,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, AmberNoticeBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            color = AmberNoticeText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = DeepInk
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MutedText
        )
    }
}

@Composable
fun TransactionRow(
    tx: EcoTransaction,
    modifier: Modifier = Modifier
) {
    val (icon, tint) = when (tx.type) {
        TransactionType.RECYCLE -> Icons.Filled.Recycling to ForestGreen
        TransactionType.COUPON -> Icons.Filled.LocalOffer to ElectricGreen
        TransactionType.CHARGING -> Icons.Filled.Bolt to Color(0xFFD97706)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MintSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = tx.type.label,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tx.description,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = DeepInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${tx.timestampLabel} · ${tx.status}",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        val deltaText = when {
            tx.pointsDelta > 0 -> "+${tx.pointsDelta} pts"
            tx.pointsDelta < 0 -> "${tx.pointsDelta} pts"
            else -> "Demo Pay"
        }
        val deltaColor = if (tx.pointsDelta >= 0) ForestGreen else DeepInk
        Text(
            text = deltaText,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = deltaColor
        )
    }
}

@Composable
fun SimpleBarChart(
    values: List<Float>,
    labels: List<String>,
    barColor: Color = ElectricGreen,
    modifier: Modifier = Modifier
) {
    val maxVal = (values.maxOrNull() ?: 1f).coerceAtLeast(1f)
    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            val count = values.size.coerceAtLeast(1)
            val spacing = 10.dp.toPx()
            val barWidth = ((size.width - spacing * (count - 1)) / count).coerceAtLeast(8f)
            values.forEachIndexed { index, v ->
                val ratio = (v / maxVal).coerceIn(0.05f, 1f)
                val barHeight = size.height * ratio
                val left = index * (barWidth + spacing)
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(left, size.height - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedText,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun DemoQrCodeCanvas(
    payload: String,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .size(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, SubtleLine, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        val gridSize = 21
        val cellSize = size.width / gridSize

        fun isFinderPattern(r: Int, c: Int): Boolean {
            val inTopLeft = r < 7 && c < 7
            val inTopRight = r < 7 && c >= gridSize - 7
            val inBottomLeft = r >= gridSize - 7 && c < 7
            if (!inTopLeft && !inTopRight && !inBottomLeft) return false
            val lr = if (r >= gridSize - 7) r - (gridSize - 7) else r
            val lc = if (c >= gridSize - 7) c - (gridSize - 7) else c
            return lr == 0 || lr == 6 || lc == 0 || lc == 6 || (lr in 2..4 && lc in 2..4)
        }

        val seed = payload.hashCode()
        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                val isFinder = isFinderPattern(r, c)
                val isDataBit = ((seed xor (r * 31 + c * 17)) and 1) == 0
                if (isFinder || (r > 7 || c > 7) && isDataBit) {
                    drawRect(
                        color = DeepInk,
                        topLeft = Offset(c * cellSize, r * cellSize),
                        size = Size(cellSize, cellSize)
                    )
                }
            }
        }
    }
}
