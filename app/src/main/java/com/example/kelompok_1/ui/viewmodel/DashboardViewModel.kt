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
    

    private val todayStart = ExpenseRepository.getStartOfDay()
    private val todayEnd = ExpenseRepository.getEndOfDay()
    

    private val weekStart = ExpenseRepository.getStartOfWeek()
    private val weekEnd = ExpenseRepository.getEndOfDay()
    

    private val monthStart = ExpenseRepository.getStartOfMonth()
    private val monthEnd = ExpenseRepository.getEndOfDay()
    

    val todayTotal: StateFlow<Double> = repository
        .getTotalByDateRange(todayStart, todayEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    

    val weeklyTotal: StateFlow<Double> = repository
        .getTotalByDateRange(weekStart, weekEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    

    val monthlyTotal: StateFlow<Double> = repository
        .getTotalByDateRange(monthStart, monthEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    

    val recentTransactions: StateFlow<List<ExpenseWithCategory>> = repository
        .getRecentExpenses(5)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    

    val dailySpending: StateFlow<List<DailySpending>> = repository
        .getDailySpending(ExpenseRepository.getDaysAgo(7), todayEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    

    val categorySpending: StateFlow<List<CategorySpending>> = repository
        .getSpendingByCategory(monthStart, monthEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    

    val monthlyIncome: StateFlow<Double> = repository
        .getTotalIncome(monthStart, monthEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    
    val monthlyExpense: StateFlow<Double> = repository
        .getTotalExpenses(monthStart, monthEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    

    private val _selectedDate = MutableStateFlow(System.currentTimeMillis())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()
    

    val expensesForSelectedDate: StateFlow<List<ExpenseWithCategory>> = _selectedDate
        .flatMapLatest { date ->
            val startOfDay = ExpenseRepository.getStartOfDay(date)
            val endOfDay = ExpenseRepository.getEndOfDay(date)
            repository.getExpensesByDateRange(startOfDay, endOfDay)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    

    val totalForSelectedDate: StateFlow<Double> = _selectedDate
        .flatMapLatest { date ->
            val startOfDay = ExpenseRepository.getStartOfDay(date)
            val endOfDay = ExpenseRepository.getEndOfDay(date)
            repository.getTotalByDateRange(startOfDay, endOfDay)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    
    fun navigateToPreviousDay() {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = _selectedDate.value
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
        _selectedDate.value = calendar.timeInMillis
    }
    
    fun navigateToNextDay() {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = _selectedDate.value
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        // Don't allow navigating to future dates
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            _selectedDate.value = calendar.timeInMillis
        }
    }
    
    fun setSelectedDate(date: Long) {
        if (date <= System.currentTimeMillis()) {
            _selectedDate.value = date
        }
    }
    

    val isToday: StateFlow<Boolean> = _selectedDate
        .map { date ->
            val todayStart = ExpenseRepository.getStartOfDay()
            val selectedStart = ExpenseRepository.getStartOfDay(date)
            todayStart == selectedStart
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    
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
