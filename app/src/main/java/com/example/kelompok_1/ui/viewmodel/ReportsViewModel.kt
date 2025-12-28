package com.example.kelompok_1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kelompok_1.data.model.CategorySpending
import com.example.kelompok_1.data.model.DailySpending
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
    
    // Date range based on selected period
    private val dateRange: StateFlow<Pair<Long, Long>> = combine(
        _selectedPeriod,
        _selectedMonth,
        _selectedYear
    ) { period, month, year ->
        calculateDateRange(period, month, year)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        calculateDateRange(ReportPeriod.MONTH, Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.YEAR))
    )
    
    // Total spending for selected period
    val totalSpending: StateFlow<Double> = dateRange
        .flatMapLatest { (start, end) ->
            repository.getTotalByDateRange(start, end)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    
    // Spending by category for selected period
    val categorySpending: StateFlow<List<CategorySpending>> = dateRange
        .flatMapLatest { (start, end) ->
            repository.getSpendingByCategory(start, end)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Daily spending trend for selected period
    val dailySpending: StateFlow<List<DailySpending>> = dateRange
        .flatMapLatest { (start, end) ->
            repository.getDailySpending(start, end)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Spending percentages by category
    val categoryPercentages: StateFlow<List<Pair<CategorySpending, Double>>> = combine(
        categorySpending,
        totalSpending
    ) { spending, total ->
        if (total > 0) {
            spending.map { it to (it.totalAmount / total * 100) }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
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
                // Go back one week
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, _selectedYear.value)
                    set(Calendar.MONTH, _selectedMonth.value)
                    add(Calendar.WEEK_OF_YEAR, -1)
                }
                _selectedMonth.value = calendar.get(Calendar.MONTH)
                _selectedYear.value = calendar.get(Calendar.YEAR)
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
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, _selectedYear.value)
                    set(Calendar.MONTH, _selectedMonth.value)
                    add(Calendar.WEEK_OF_YEAR, 1)
                }
                _selectedMonth.value = calendar.get(Calendar.MONTH)
                _selectedYear.value = calendar.get(Calendar.YEAR)
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
    
    private fun calculateDateRange(period: ReportPeriod, month: Int, year: Int): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        
        return when (period) {
            ReportPeriod.WEEK -> {
                calendar.apply {
                    set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
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
