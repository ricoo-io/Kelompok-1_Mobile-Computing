package com.example.kelompok_1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kelompok_1.data.model.CategorySpending
import com.example.kelompok_1.data.model.DailySpending
import com.example.kelompok_1.data.model.ExpenseWithCategory
import com.example.kelompok_1.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import java.util.*

enum class ReportPeriod(val label: String) {
    WEEK("Minggu"),
    MONTH("Bulan"),
    YEAR("Tahun")
}

class ReportsViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {
    
    private val _selectedPeriod = MutableStateFlow(ReportPeriod.MONTH)
    val selectedPeriod: StateFlow<ReportPeriod> = _selectedPeriod.asStateFlow()
    
    private val _selectedMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))
    val selectedMonth: StateFlow<Int> = _selectedMonth.asStateFlow()
    
    private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()
    
    private val _selectedWeekOffset = MutableStateFlow(0)
    val selectedWeekOffset: StateFlow<Int> = _selectedWeekOffset.asStateFlow()

    private val dateRange: StateFlow<Pair<Long, Long>> = combine(
        _selectedPeriod,
        _selectedMonth,
        _selectedYear,
        _selectedWeekOffset
    ) { period, month, year, weekOffset ->
        calculateDateRange(period, month, year, weekOffset)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        calculateDateRange(ReportPeriod.MONTH, Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.YEAR), 0)
    )
    

    val totalSpending: StateFlow<Double> = dateRange
        .flatMapLatest { (start, end) ->
            repository.getTotalByDateRange(start, end)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    

    val totalIncome: StateFlow<Double> = dateRange
        .flatMapLatest { (start, end) ->
            repository.getTotalIncome(start, end)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    

    val totalExpense: StateFlow<Double> = dateRange
        .flatMapLatest { (start, end) ->
            repository.getTotalExpenses(start, end)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    

    val categorySpending: StateFlow<List<CategorySpending>> = dateRange
        .flatMapLatest { (start, end) ->
            repository.getSpendingByCategory(start, end)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    

    val dailySpending: StateFlow<List<DailySpending>> = dateRange
        .flatMapLatest { (start, end) ->
            repository.getDailySpending(start, end)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Toggle for showing income vs expense
    private val _showIncome = MutableStateFlow(false)
    val showIncome: StateFlow<Boolean> = _showIncome.asStateFlow()
    
    // Income by category
    val categoryIncome: StateFlow<List<CategorySpending>> = dateRange
        .flatMapLatest { (start, end) ->
            repository.getIncomeByCategory(start, end)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Income percentages
    val categoryIncomePercentages: StateFlow<List<Pair<CategorySpending, Double>>> = combine(
        categoryIncome,
        totalIncome
    ) { income, total ->
        if (total > 0) {
            income.map { it to (it.totalAmount / total * 100) }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    fun toggleShowIncome(showIncome: Boolean) {
        _showIncome.value = showIncome
    }
    

    val categoryPercentages: StateFlow<List<Pair<CategorySpending, Double>>> = combine(
        categorySpending,
        totalExpense
    ) { spending, total ->
        if (total > 0) {
            spending.map { it to (it.totalAmount / total * 100) }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    fun getExpensesForCategory(categoryId: Long): Flow<List<ExpenseWithCategory>> {
        return dateRange.flatMapLatest { (start, end) ->
            repository.getExpensesByCategory(categoryId, start, end)
        }
    }
    
    fun selectPeriod(period: ReportPeriod) {
        _selectedPeriod.value = period
    }
    
    fun selectMonth(month: Int) {
        _selectedMonth.value = month
    }
    
    fun selectYear(year: Int) {
        _selectedYear.value = year
    }
    
    fun previousPeriod() {
        when (_selectedPeriod.value) {
            ReportPeriod.WEEK -> {
                _selectedWeekOffset.value = _selectedWeekOffset.value - 1
            }
            ReportPeriod.MONTH -> {
                if (_selectedMonth.value == 0) {
                    _selectedMonth.value = 11
                    _selectedYear.value = _selectedYear.value - 1
                } else {
                    _selectedMonth.value = _selectedMonth.value - 1
                }
            }
            ReportPeriod.YEAR -> {
                _selectedYear.value = _selectedYear.value - 1
            }
        }
    }
    
    fun nextPeriod() {
        when (_selectedPeriod.value) {
            ReportPeriod.WEEK -> {
                _selectedWeekOffset.value = _selectedWeekOffset.value + 1
            }
            ReportPeriod.MONTH -> {
                if (_selectedMonth.value == 11) {
                    _selectedMonth.value = 0
                    _selectedYear.value = _selectedYear.value + 1
                } else {
                    _selectedMonth.value = _selectedMonth.value + 1
                }
            }
            ReportPeriod.YEAR -> {
                _selectedYear.value = _selectedYear.value + 1
            }
        }
    }
    
    private fun calculateDateRange(period: ReportPeriod, month: Int, year: Int, weekOffset: Int = 0): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        
        return when (period) {
            ReportPeriod.WEEK -> {
                calendar.apply {
                    set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    add(Calendar.WEEK_OF_YEAR, weekOffset)
                }
                val start = calendar.timeInMillis
                calendar.add(Calendar.DAY_OF_WEEK, 6)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                val end = calendar.timeInMillis
                start to end
            }
            ReportPeriod.MONTH -> {
                calendar.apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val start = calendar.timeInMillis
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                val end = calendar.timeInMillis
                start to end
            }
            ReportPeriod.YEAR -> {
                calendar.apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val start = calendar.timeInMillis
                calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR))
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                val end = calendar.timeInMillis
                start to end
            }
        }
    }
    
    fun getMonthName(month: Int): String {
        val months = listOf(
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        )
        return months[month]
    }
    
    fun getWeekDateRangeText(weekOffset: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            add(Calendar.WEEK_OF_YEAR, weekOffset)
        }
        val startDay = calendar.get(Calendar.DAY_OF_MONTH)
        val startMonth = calendar.get(Calendar.MONTH)
        val startYear = calendar.get(Calendar.YEAR)
        
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endDay = calendar.get(Calendar.DAY_OF_MONTH)
        val endMonth = calendar.get(Calendar.MONTH)
        val endYear = calendar.get(Calendar.YEAR)
        
        val months = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
        
        return if (startMonth == endMonth && startYear == endYear) {
            "$startDay - $endDay ${months[startMonth]} $startYear"
        } else if (startYear == endYear) {
            "$startDay ${months[startMonth]} - $endDay ${months[endMonth]} $startYear"
        } else {
            "$startDay ${months[startMonth]} $startYear - $endDay ${months[endMonth]} $endYear"
        }
    }
    
    class Factory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReportsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReportsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
