package com.example.kelompok_1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kelompok_1.data.model.Category
import com.example.kelompok_1.data.model.ExpenseWithCategory
import com.example.kelompok_1.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class HistoryFilterState(
    val searchQuery: String = "",
    val selectedCategoryId: Long? = null,
    val startDate: Long? = null,
    val endDate: Long? = null
)

class HistoryViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {
    
    private val _filterState = MutableStateFlow(HistoryFilterState())
    val filterState: StateFlow<HistoryFilterState> = _filterState.asStateFlow()
    
    val categories: StateFlow<List<Category>> = repository
        .getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    

    private val allExpenses: StateFlow<List<ExpenseWithCategory>> = repository
        .getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    

    val filteredExpenses: StateFlow<List<ExpenseWithCategory>> = combine(
        allExpenses,
        _filterState
    ) { expenses, filter ->
        expenses.filter { expense ->
            val matchesSearch = filter.searchQuery.isEmpty() ||
                expense.name.contains(filter.searchQuery, ignoreCase = true) ||
                expense.description.contains(filter.searchQuery, ignoreCase = true) ||
                expense.categoryName.contains(filter.searchQuery, ignoreCase = true)
            
            val matchesCategory = filter.selectedCategoryId == null ||
                expense.categoryId == filter.selectedCategoryId
            
            val matchesDateRange = (filter.startDate == null || expense.date >= filter.startDate) &&
                (filter.endDate == null || expense.date <= filter.endDate)
            
            matchesSearch && matchesCategory && matchesDateRange
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    

    val groupedExpenses: StateFlow<Map<String, List<ExpenseWithCategory>>> = filteredExpenses
        .map { expenses ->
            expenses.groupBy { expense ->
                formatDateGroup(expense.date)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
    

    val filteredTotal: StateFlow<Double> = filteredExpenses
        .map { expenses -> expenses.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    
    fun updateSearchQuery(query: String) {
        _filterState.update { it.copy(searchQuery = query) }
    }
    
    fun selectCategory(categoryId: Long?) {
        _filterState.update { it.copy(selectedCategoryId = categoryId) }
    }
    
    fun setDateRange(startDate: Long?, endDate: Long?) {
        _filterState.update { it.copy(startDate = startDate, endDate = endDate) }
    }
    
    fun clearFilters() {
        _filterState.value = HistoryFilterState()
    }
    
    fun setThisMonthFilter() {
        _filterState.update { 
            it.copy(
                startDate = ExpenseRepository.getStartOfMonth(),
                endDate = ExpenseRepository.getEndOfDay()
            )
        }
    }
    
    fun deleteExpense(expenseId: Long) {
        viewModelScope.launch {
            repository.deleteExpenseById(expenseId)
        }
    }
    
    private fun formatDateGroup(timestamp: Long): String {
        val today = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }
        
        return when {
            isSameDay(today, date) -> "HARI INI"
            isYesterday(today, date) -> "KEMARIN"
            else -> {
                val sdf = java.text.SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
                sdf.format(Date(timestamp)).uppercase()
            }
        }
    }
    
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    
    private fun isYesterday(today: Calendar, date: Calendar): Boolean {
        val yesterday = today.clone() as Calendar
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        return isSameDay(yesterday, date)
    }
    
    class Factory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HistoryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
