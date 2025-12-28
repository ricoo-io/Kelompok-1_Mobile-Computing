package com.example.kelompok_1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kelompok_1.data.model.Category
import com.example.kelompok_1.data.model.Expense
import com.example.kelompok_1.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class ExpenseFormState(
    val amount: String = "",
    val description: String = "",
    val selectedCategoryId: Long? = null,
    val date: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

class ExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {
    
    private val _formState = MutableStateFlow(ExpenseFormState())
    val formState: StateFlow<ExpenseFormState> = _formState.asStateFlow()
    
    val categories: StateFlow<List<Category>> = repository
        .getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    fun updateAmount(amount: String) {
        // Only allow numeric input
        val filtered = amount.filter { it.isDigit() }
        _formState.update { it.copy(amount = filtered, error = null) }
    }
    
    fun updateDescription(description: String) {
        _formState.update { it.copy(description = description, error = null) }
    }
    
    fun selectCategory(categoryId: Long) {
        _formState.update { it.copy(selectedCategoryId = categoryId, error = null) }
    }
    
    fun updateDate(date: Long) {
        _formState.update { it.copy(date = date, error = null) }
    }
    
    fun saveExpense() {
        val state = _formState.value
        
        // Validation
        when {
            state.amount.isBlank() || state.amount.toDoubleOrNull() == null || state.amount.toDouble() <= 0 -> {
                _formState.update { it.copy(error = "Masukkan jumlah yang valid") }
                return
            }
            state.description.isBlank() -> {
                _formState.update { it.copy(error = "Masukkan deskripsi") }
                return
            }
            state.selectedCategoryId == null -> {
                _formState.update { it.copy(error = "Pilih kategori") }
                return
            }
        }
        
        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true) }
            
            try {
                val expense = Expense(
                    amount = state.amount.toDouble(),
                    description = state.description,
                    categoryId = state.selectedCategoryId!!,
                    date = state.date,
                    createdAt = System.currentTimeMillis()
                )
                
                repository.insertExpense(expense)
                _formState.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _formState.update { 
                    it.copy(isLoading = false, error = e.message ?: "Gagal menyimpan") 
                }
            }
        }
    }
    
    fun resetForm() {
        _formState.value = ExpenseFormState()
    }
    
    class Factory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ExpenseViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
