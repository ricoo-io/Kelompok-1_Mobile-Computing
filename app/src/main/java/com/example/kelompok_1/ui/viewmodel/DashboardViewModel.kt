package com.example.kelompok_1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kelompok_1.data.model.CategorySpending
import com.example.kelompok_1.data.model.DailySpending
import com.example.kelompok_1.data.model.ExpenseWithCategory
import com.example.kelompok_1.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {
    
    private val _selectedPeriod = MutableStateFlow("Weekly")
    val selectedPeriod: StateFlow<String> = _selectedPeriod.asStateFlow()
    
    // Today's date range
    private val todayStart = ExpenseRepository.getStartOfDay()
    private val todayEnd = ExpenseRepository.getEndOfDay()
    
    // Week date range
    private val weekStart = ExpenseRepository.getStartOfWeek()
    private val weekEnd = ExpenseRepository.getEndOfDay()
    
    // Month date range
    private val monthStart = ExpenseRepository.getStartOfMonth()
    private val monthEnd = ExpenseRepository.getEndOfDay()
    
    // Daily total
    val todayTotal: StateFlow<Double> = repository
        .getTotalByDateRange(todayStart, todayEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    
    // Weekly total
    val weeklyTotal: StateFlow<Double> = repository
        .getTotalByDateRange(weekStart, weekEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    
    // Monthly total
    val monthlyTotal: StateFlow<Double> = repository
        .getTotalByDateRange(monthStart, monthEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    
    // Recent transactions (last 5)
    val recentTransactions: StateFlow<List<ExpenseWithCategory>> = repository
        .getRecentExpenses(5)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Daily spending for chart (last 7 days)
    val dailySpending: StateFlow<List<DailySpending>> = repository
        .getDailySpending(ExpenseRepository.getDaysAgo(7), todayEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Spending by category this month
    val categorySpending: StateFlow<List<CategorySpending>> = repository
        .getSpendingByCategory(monthStart, monthEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    fun selectPeriod(period: String) {
        _selectedPeriod.value = period
    }
    
    fun getSelectedPeriodTotal(): Flow<Double> {
        return when (_selectedPeriod.value) {
            "Daily" -> repository.getTotalByDateRange(todayStart, todayEnd)
            "Weekly" -> repository.getTotalByDateRange(weekStart, weekEnd)
            "Monthly" -> repository.getTotalByDateRange(monthStart, monthEnd)
            else -> repository.getTotalByDateRange(weekStart, weekEnd)
        }
    }
    
    class Factory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DashboardViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
