package com.example.kelompok_1.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDatePicker(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val calendar = remember(selectedDate) {
        Calendar.getInstance().apply { timeInMillis = selectedDate }
    }
    
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH)
    val year = calendar.get(Calendar.YEAR)
    
    val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
        "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
    )
    
    Column(modifier = modifier) {
        Text(
            text = "Tanggal Transaksi",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Day dropdown
            var dayExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = dayExpanded,
                onExpandedChange = { dayExpanded = it },
                modifier = Modifier.weight(0.8f)
            ) {
                OutlinedTextField(
                    value = day.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tgl") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth(),
                    singleLine = true,
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = dayExpanded,
                    onDismissRequest = { dayExpanded = false }
                ) {
                    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                    (1..daysInMonth).forEach { d ->
                        DropdownMenuItem(
                            text = { Text(d.toString()) },
                            onClick = {
                                val newCalendar = Calendar.getInstance().apply {
                                    set(year, month, d)
                                }
                                onDateSelected(newCalendar.timeInMillis)
                                dayExpanded = false
                            }
                        )
                    }
                }
            }

            // Month dropdown
            var monthExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = monthExpanded,
                onExpandedChange = { monthExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = months[month],
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Bulan") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth(),
                    singleLine = true,
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = monthExpanded,
                    onDismissRequest = { monthExpanded = false }
                ) {
                    months.forEachIndexed { index, monthName ->
                        DropdownMenuItem(
                            text = { Text(monthName) },
                            onClick = {
                                val tempCalendar = Calendar.getInstance().apply {
                                    set(year, index, 1)
                                }
                                val maxDay = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                                val newCalendar = Calendar.getInstance().apply {
                                    set(year, index, day.coerceAtMost(maxDay))
                                }
                                onDateSelected(newCalendar.timeInMillis)
                                monthExpanded = false
                            }
                        )
                    }
                }
            }

            // Year dropdown
            var yearExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = yearExpanded,
                onExpandedChange = { yearExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = year.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tahun") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth(),
                    singleLine = true,
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = yearExpanded,
                    onDismissRequest = { yearExpanded = false }
                ) {
                    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                    (currentYear - 5..currentYear + 1).forEach { y ->
                        DropdownMenuItem(
                            text = { Text(y.toString()) },
                            onClick = {
                                val tempCalendar = Calendar.getInstance().apply {
                                    set(y, month, 1)
                                }
                                val maxDay = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                                val newCalendar = Calendar.getInstance().apply {
                                    set(y, month, day.coerceAtMost(maxDay))
                                }
                                onDateSelected(newCalendar.timeInMillis)
                                yearExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

