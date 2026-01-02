package com.example.kelompok_1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kelompok_1.data.model.CategorySpending
import java.text.NumberFormat
import java.util.*
import kotlin.math.abs

@Composable
fun StatsCard(
    topCategory: CategorySpending?,
    expenseChangePercent: Double,
    averageDailySpending: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Statistik",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickStatItem(
                    icon = if (expenseChangePercent >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    iconColor = if (expenseChangePercent >= 0) MaterialTheme.colorScheme.error else Color(0xFF4CAF50),
                    label = "vs Bulan Lalu",
                    value = "${if (expenseChangePercent >= 0) "+" else ""}${expenseChangePercent.toInt()}%",
                    valueColor = if (expenseChangePercent >= 0) MaterialTheme.colorScheme.error else Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                   
                QuickStatItem(
                    icon = Icons.Default.CalendarToday,
                    iconColor = MaterialTheme.colorScheme.primary,
                    label = "Rata-rata/Hari",
                    value = formatCurrencyShort(averageDailySpending),
                    valueColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
           
            if (topCategory != null) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(topCategory.categoryColor).copy(alpha = 0.1f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(topCategory.categoryColor).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(topCategory.categoryIcon),
                            contentDescription = null,
                            tint = Color(topCategory.categoryColor),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Pengeluaran Terbesar",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = topCategory.categoryName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    Text(
                        text = formatCurrencyShort(topCategory.totalAmount),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(topCategory.categoryColor)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStatItem(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

private fun formatCurrencyShort(amount: Double): String {
    return when {
        amount >= 1_000_000_000 -> String.format("Rp%.1fM", amount / 1_000_000_000)
        amount >= 1_000_000 -> String.format("Rp%.1fJt", amount / 1_000_000)
        amount >= 1_000 -> String.format("Rp%.0fRb", amount / 1_000)
        else -> {
            val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            format.maximumFractionDigits = 0
            format.format(amount)
        }
    }
}
