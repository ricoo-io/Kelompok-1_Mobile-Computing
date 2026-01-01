package com.example.kelompok_1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kelompok_1.data.model.Budget
import com.example.kelompok_1.data.model.Category
import com.example.kelompok_1.data.model.CategorySpending
import com.example.kelompok_1.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class CategoryWithBudget(
    val category: Category,
    val budgetAmount: Double,
    val spentAmount: Double
)

class CategoryViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {
    
    private val currentCalendar = Calendar.getInstance()
    private val currentMonth = currentCalendar.get(Calendar.MONTH) + 1
    private val currentYear = currentCalendar.get(Calendar.YEAR)
    
    private val monthStart = ExpenseRepository.getStartOfMonth()
    private val monthEnd = ExpenseRepository.getEndOfDay()
    
    val categories: StateFlow<List<Category>> = repository
        .getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val budgets: StateFlow<List<Budget>> = repository
        .getBudgetsByMonth(currentMonth, currentYear)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val categorySpending: StateFlow<List<CategorySpending>> = repository
        .getSpendingByCategory(monthStart, monthEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val totalBudget: StateFlow<Double> = repository
        .getTotalBudget(currentMonth, currentYear)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    

    val categoriesWithBudget: StateFlow<List<CategoryWithBudget>> = combine(
        categories,
        budgets,
        categorySpending
    ) { cats, buds, spending ->
        cats.map { category ->
            val budget = buds.find { it.categoryId == category.id }?.amount ?: 0.0
            val spent = spending.find { it.categoryId == category.id }?.totalAmount ?: 0.0
            CategoryWithBudget(category, budget, spent)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()
    
    private val _showEditDialog = MutableStateFlow<Category?>(null)
    val showEditDialog: StateFlow<Category?> = _showEditDialog.asStateFlow()
    
    private val _showBudgetDialog = MutableStateFlow<Category?>(null)
    val showBudgetDialog: StateFlow<Category?> = _showBudgetDialog.asStateFlow()
    
    fun showAddCategoryDialog() {
        _showAddDialog.value = true
    }
    
    fun hideAddCategoryDialog() {
        _showAddDialog.value = false
    }
    
    fun showEditCategoryDialog(category: Category) {
        _showEditDialog.value = category
    }
    
    fun hideEditCategoryDialog() {
        _showEditDialog.value = null
    }
    
    fun showBudgetDialog(category: Category) {
        _showBudgetDialog.value = category
    }
    
    fun hideBudgetDialog() {
        _showBudgetDialog.value = null
    }
    
    fun addCategory(name: String, icon: String, color: Long) {
        viewModelScope.launch {
            val category = Category(
                name = name,
                icon = icon,
                color = color
            )
            repository.insertCategory(category)
            hideAddCategoryDialog()
        }
    }
    
    fun updateCategory(category: Category, name: String, icon: String, color: Long) {
        viewModelScope.launch {
            val updated = category.copy(name = name, icon = icon, color = color)
            repository.updateCategory(updated)
            hideEditCategoryDialog()
        }
    }
    
    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            hideEditCategoryDialog()
        }
    }
    
    fun setBudget(categoryId: Long, amount: Double) {
        viewModelScope.launch {
            val existingBudget = repository.getBudget(categoryId, currentMonth, currentYear)
            if (existingBudget != null) {
                repository.updateBudget(existingBudget.copy(amount = amount))
            } else {
                repository.insertBudget(
                    Budget(
                        categoryId = categoryId,
                        amount = amount,
                        month = currentMonth,
                        year = currentYear
                    )
                )
            }
            hideBudgetDialog()
        }
    }
    
    class Factory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CategoryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
