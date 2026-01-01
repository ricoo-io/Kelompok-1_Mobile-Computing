package com.example.kelompok_1.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kelompok_1.data.model.CategorySpending
import com.example.kelompok_1.data.model.DailySpending
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SpendingTrendChart(
    dailySpending: List<DailySpending>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Spending Trend",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Details >",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (dailySpending.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data available",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LineChart(
                    data = dailySpending,
                    lineColor = primaryColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    dailySpending.takeLast(7).forEach { daily ->
                        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
                        Text(
                            text = sdf.format(Date(daily.date)),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LineChart(
    data: List<DailySpending>,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return
    
    val maxAmount = data.maxOfOrNull { it.totalAmount } ?: 1.0
    
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 20f
        
        val chartWidth = width - padding * 2
        val chartHeight = height - padding * 2
        
        val points = data.mapIndexed { index, daily ->
            val x = padding + (index.toFloat() / (data.size - 1).coerceAtLeast(1)) * chartWidth
            val y = padding + chartHeight - (daily.totalAmount / maxAmount * chartHeight).toFloat()
            Offset(x, y)
        }

        if (points.size > 1) {
            val path = Path().apply {
                moveTo(points.first().x, height - padding)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, height - padding)
                close()
            }
            
            drawPath(
                path = path,
                color = lineColor.copy(alpha = 0.1f)
            )

            val linePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { point ->
                    lineTo(point.x, point.y)
                }
            }
            
            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(width = 3f)
            )

            points.forEach { point ->
                drawCircle(
                    color = lineColor,
                    radius = 6f,
                    center = point
                )
                drawCircle(
                    color = Color.White,
                    radius = 3f,
                    center = point
                )
            }
        }
    }
}

@Composable
fun CategoryPieChart(
    categorySpending: List<CategorySpending>,
    modifier: Modifier = Modifier
) {
    if (categorySpending.isEmpty()) {
        Box(
            modifier = modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No data",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }
    
    val total = categorySpending.sumOf { it.totalAmount }
    val surfaceColor = MaterialTheme.colorScheme.surface
    
    Canvas(modifier = modifier.size(200.dp)) {
        val diameter = size.minDimension
        val radius = diameter / 2
        val centerX = size.width / 2
        val centerY = size.height / 2
        
        var startAngle = -90f
        
        categorySpending.forEach { spending ->
            val sweepAngle = (spending.totalAmount / total * 360).toFloat()
            
            drawArc(
                color = Color(spending.categoryColor),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = androidx.compose.ui.geometry.Size(diameter, diameter)
            )
            
            startAngle += sweepAngle
        }

        drawCircle(
            color = surfaceColor,
            radius = radius * 0.5f,
            center = Offset(centerX, centerY)
        )
    }
}

@Composable
fun CategoryBarChart(
    categorySpending: List<CategorySpending>,
    modifier: Modifier = Modifier
) {
    if (categorySpending.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No data available",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }
    
    val maxAmount = categorySpending.maxOfOrNull { it.totalAmount } ?: 1.0
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        categorySpending.forEach { spending ->
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    Color(spending.categoryColor),
                                    RoundedCornerShape(3.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = spending.categoryName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = formatCurrency(spending.totalAmount),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(4.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((spending.totalAmount / maxAmount).toFloat())
                            .height(8.dp)
                            .background(
                                Color(spending.categoryColor),
                                RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }
    }
}
