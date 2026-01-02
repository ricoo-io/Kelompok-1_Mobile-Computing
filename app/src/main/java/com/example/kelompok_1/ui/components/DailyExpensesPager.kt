package com.example.kelompok_1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kelompok_1.data.model.ExpenseWithCategory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DailyExpensesPager(
    selectedDate: Long,
    expenses: List<ExpenseWithCategory>,
    totalForDay: Double,
    isToday: Boolean,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                IconButton(onClick = onPreviousDay) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Hari sebelumnya",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatDateHeader(selectedDate, isToday),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatFullDate(selectedDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(
                    onClick = onNextDay,
                    enabled = !isToday
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Hari berikutnya",
                        tint = if (isToday) 
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        else 
                            MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            val dailyIncome = expenses.filter { it.isIncome }.sumOf { it.amount }
            val dailyExpense = expenses.filter { !it.isIncome }.sumOf { it.amount }
            

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFF4CAF50).copy(alpha = 0.15f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Pemasukan",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = formatCurrencyRupiah(dailyIncome),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.ui.graphics.Color(0xFF4CAF50)
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Pengeluaran",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = formatCurrencyRupiah(dailyExpense),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            if (expenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tidak ada transaksi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    expenses.take(5).forEach { expense ->
                        DailyExpenseItem(expense = expense)
                    }
                    if (expenses.size > 5) {
                        Text(
                            text = "+ ${expenses.size - 5} transaksi lainnya",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyExpenseItem(
    expense: ExpenseWithCategory,
    modifier: Modifier = Modifier
) {
    val amountColor = if (expense.isIncome) 
        androidx.compose.ui.graphics.Color(0xFF4CAF50) 
    else 
        MaterialTheme.colorScheme.error
    val prefix = if (expense.isIncome) "+" else "-"
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.name.ifEmpty { expense.categoryName },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = expense.categoryName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "$prefix${formatCurrencyRupiah(expense.amount)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = amountColor
        )
    }
}

private fun formatDateHeader(timestamp: Long, isToday: Boolean): String {
    if (isToday) return "Hari Ini"
    
    val calendar = Calendar.getInstance()
    val today = Calendar.getInstance()
    calendar.timeInMillis = timestamp

    today.add(Calendar.DAY_OF_YEAR, -1)
    if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
        return "Kemarin"
    }

    val sdf = SimpleDateFormat("EEEE", Locale("id", "ID"))
    return sdf.format(Date(timestamp))
}

private fun formatFullDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    return sdf.format(Date(timestamp))
}

private fun formatCurrencyRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}
