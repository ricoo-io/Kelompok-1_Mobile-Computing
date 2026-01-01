package com.example.kelompok_1.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kelompok_1.ExpenseTrackerApplication
import com.example.kelompok_1.data.model.ExpenseWithCategory
import com.example.kelompok_1.ui.components.*
import com.example.kelompok_1.ui.theme.*
import com.example.kelompok_1.ui.viewmodel.HistoryViewModel

class HistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        overridePendingTransition(0, 0)
        
        val repository = (application as ExpenseTrackerApplication).repository
        
        setContent {
            val isDarkMode by ThemePreferences.isDarkMode(this).collectAsState(initial = false)
            
            ExpenseTrackerTheme(darkTheme = isDarkMode) {
                val viewModel: HistoryViewModel = viewModel(
                    factory = HistoryViewModel.Factory(repository)
                )
                HistoryScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val filterState by viewModel.filterState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val groupedExpenses by viewModel.groupedExpenses.collectAsState()
    val filteredTotal by viewModel.filteredTotal.collectAsState()
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Riwayat Transaksi",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = {  }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            BottomNavBar(currentRoute = "history")
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = filterState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Cari transaksi...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (filterState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                singleLine = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterState.selectedCategoryId == null && filterState.startDate == null,
                    onClick = { viewModel.clearFilters() },
                    label = { Text("Semua") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )

                FilterChip(
                    selected = filterState.startDate != null,
                    onClick = {
                        if (filterState.startDate != null) {
                            viewModel.setDateRange(null, null)
                        } else {
                            viewModel.setThisMonthFilter()
                        }
                    },
                    label = { Text("Bulan ini") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )

                categories.take(3).forEach { category ->
                    FilterChip(
                        selected = filterState.selectedCategoryId == category.id,
                        onClick = {
                            if (filterState.selectedCategoryId == category.id) {
                                viewModel.selectCategory(null)
                            } else {
                                viewModel.selectCategory(category.id)
                            }
                        },
                        label = { Text(category.name.split(" ").first()) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(category.color),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (groupedExpenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tidak ada transaksi",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Transaksi Anda akan muncul di sini",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    groupedExpenses.forEach { (dateGroup, expenses) ->
                        val dayIncome = expenses.filter { it.isIncome }.sumOf { it.amount }
                        val dayExpense = expenses.filter { !it.isIncome }.sumOf { it.amount }
                        val dayNet = dayIncome - dayExpense

                        item {
                            TransactionDateHeader(
                                date = dateGroup,
                                totalAmount = dayNet,
                                isPositive = dayNet >= 0
                            )
                        }

                        items(expenses, key = { it.id }) { expense ->
                            TransactionItem(
                                expense = expense,
                                onClick = {
                                    val intent = android.content.Intent(
                                        context,
                                        AddExpenseActivity::class.java
                                    )
                                    intent.putExtra(AddExpenseActivity.EXTRA_EXPENSE_ID, expense.id)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            val allExpenses = groupedExpenses.values.flatten()
                            val totalIncome = allExpenses.filter { it.isIncome }.sumOf { it.amount }
                            val totalExpenseAmount =
                                allExpenses.filter { !it.isIncome }.sumOf { it.amount }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total Pemasukan",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "+${formatCurrency(totalIncome)}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total Pengeluaran",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "-${formatCurrency(totalExpenseAmount)}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
