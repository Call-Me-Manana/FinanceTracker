package com.example.financetracker.ui.analytics.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.financetracker.data.model.CategoryExpense
import kotlin.math.min

@Composable
fun ExpensesDonutChart(
    data: List<CategoryExpense>,
    modifier: Modifier = Modifier
) {
    val safe = remember(data) { data.filter { it.amount > 0 } }

    if (safe.isEmpty()) return

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val total = safe.sumOf { it.amount }.toFloat()

    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(900),
        label = "donutProgress"
    )

    val colors = listOf(
        Color(0xFF2ECC71),
        Color(0xFFFFB020),
        Color(0xFF4A90E2),
        Color(0xFFFF5A5F),
        Color(0xFF9B59B6),
        Color(0xFF00BCD4)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->

                        val index = (offset.x / size.width * safe.size)
                            .toInt()
                            .coerceIn(0, safe.lastIndex)

                        selectedIndex = index
                    }
                }
        ) {

            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {

                val diameter = min(size.width, size.height)
                val radius = diameter / 2f
                val center = this.center

                val innerRadius = radius * 0.62f
                val gap = 3f

                var startAngle = -90f

                safe.forEachIndexed { index, item ->

                    val sweepRaw =
                        (item.amount.toFloat() / total) *
                                360f *
                                progress

                    val sweep = sweepRaw - gap

                    val isSelected = selectedIndex == index

                    val scale =
                        if (isSelected) 1.03f else 1f

                    val alpha =
                        if (selectedIndex == null || isSelected)
                            1f
                        else
                            0.25f

                    drawArc(
                        color = colors[index % colors.size]
                            .copy(alpha = alpha),
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = true,
                        topLeft = Offset(
                            center.x - radius * scale,
                            center.y - radius * scale
                        ),
                        size = Size(
                            diameter * scale,
                            diameter * scale
                        )
                    )

                    startAngle += sweep + gap
                }

                // donut hole
                drawCircle(
                    color = Color.White,
                    radius = innerRadius,
                    center = center
                )
            }

            val selected =
                selectedIndex?.let { safe[it] }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = selected?.category ?: "Расходы",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "${(selected?.amount ?: total.toDouble()).toInt()} ₽",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // ЛЕГЕНДА
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            safe.forEachIndexed { index, item ->

                val isSelected =
                    selectedIndex == index

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedIndex = index
                        }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(
                                color = colors[index % colors.size],
                                shape = CircleShape
                            )
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Text(
                        text = item.category,
                        style = MaterialTheme.typography.bodyLarge,
                        color =
                            if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "${item.amount.toInt()} ₽",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}