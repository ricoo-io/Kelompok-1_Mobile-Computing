package com.example.kelompok_1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kelompok_1.ui.activity.*
import com.example.kelompok_1.ui.components.*
import com.example.kelompok_1.ui.theme.*
import com.example.kelompok_1.ui.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        overridePendingTransition(0, 0)

        val repository = (application as ExpenseTrackerApplication).repository
        val initialDarkMode = ThemePreferences.isDarkModeBlocking(this)

        setContent {
            val context = LocalContext.current
            val isDarkMode by ThemePreferences.isDarkMode(context).collectAsState(initial = initialDarkMode)
            val scope = rememberCoroutineScope()

            ExpenseTrackerTheme(darkTheme = isDarkMode) {
                val viewModel: DashboardViewModel = viewModel(
                    factory = DashboardViewModel.Factory(repository)
                )
                MainScreen(
                    viewModel = viewModel,
                    isDarkMode = isDarkMode,
                    onToggleTheme = {
                        scope.launch {
                            ThemePreferences.setDarkMode(context, !isDarkMode)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: DashboardViewModel,
    isDarkMode: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {
    val context = LocalContext.current
    val todayTotal by viewModel.todayTotal.collectAsState()
    val weeklyTotal by viewModel.weeklyTotal.collectAsState()
    val monthlyTotal by viewModel.monthlyTotal.collectAsState()
    val monthlyIncome by viewModel.monthlyIncome.collectAsState()
    val monthlyExpense by viewModel.monthlyExpense.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()

    val topCategory by viewModel.topCategory.collectAsState()
    val expenseChangePercent by viewModel.expenseChangePercent.collectAsState()
    val averageDailySpending by viewModel.averageDailySpending.collectAsState()

    val selectedDate by viewModel.selectedDate.collectAsState()
    val expensesForSelectedDate by viewModel.expensesForSelectedDate.collectAsState()
    val totalForSelectedDate by viewModel.totalForSelectedDate.collectAsState()
    val isToday by viewModel.isToday.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavBar(currentRoute = "dashboard")
        },
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(
                            id = if (isDarkMode) R.drawable.logo_pandana_dark
                                 else R.drawable.logo_pandana_light
                        ),
                        contentDescription = "PanDana Logo",
                        modifier = Modifier.height(36.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkMode) "Light Mode" else "Dark Mode",
                            tint = if (isDarkMode) LightBlueAccent else MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        )
                        .padding(vertical = 24.dp)
                        .padding(horizontal = 20.dp)
                ) {
                    BalanceCard(
                        totalBalance = monthlyIncome - monthlyExpense,
                        income = monthlyIncome,
                        expense = monthlyExpense
                    )
                }
            }
            
            item {
                StatsCard(
                    topCategory = topCategory,
                    expenseChangePercent = expenseChangePercent,
                    averageDailySpending = averageDailySpending,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                DailyExpensesPager(
                    selectedDate = selectedDate,
                    expenses = expensesForSelectedDate,
                    totalForDay = totalForSelectedDate,
                    isToday = isToday,
                    onPreviousDay = { viewModel.navigateToPreviousDay() },
                    onNextDay = { viewModel.navigateToNextDay() },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transaksi Terbaru",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = {
                        context.startActivity(Intent(context, HistoryActivity::class.java))
                    }) {
                        Text(
                            text = "Lihat Semua",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (recentTransactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
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
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Belum ada transaksi",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
            items(recentTransactions) { expense ->
                    TransactionItem(
                        expense = expense,
                        onClick = {
                            val intent = Intent(context, AddExpenseActivity::class.java)
                            intent.putExtra(AddExpenseActivity.EXTRA_EXPENSE_ID, expense.id)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
