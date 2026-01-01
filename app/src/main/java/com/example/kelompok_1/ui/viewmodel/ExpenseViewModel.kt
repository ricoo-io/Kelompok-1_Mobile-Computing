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
    val editingId: Long? = null,
    val amount: String = "",
    val name: String = "", // Nama transaksi (wajib)
    val description: String = "", // Catatan (opsional)
    val selectedCategoryId: Long? = null,
    val date: Long = System.currentTimeMillis(),
    val isIncome: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false,
    val error: String? = null
)

class ExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {
    
    private val _formState = MutableStateFlow(ExpenseFormState())
    val formState: StateFlow<ExpenseFormState> = _formState.asStateFlow()
    

    val categories: StateFlow<List<Category>> = _formState
        .map { it.isIncome }
        .distinctUntilChanged()
        .flatMapLatest { isIncome ->
            repository.getCategoriesByType(if (isIncome) "income" else "expense")
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    fun loadExpense(expenseId: Long) {
        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true) }
            try {
                repository.getExpenseById(expenseId)?.let { expense ->
                    _formState.update {
                        it.copy(
                            editingId = expense.id,
                            amount = expense.amount.toLong().toString(),
                            name = expense.name,
                            description = expense.description,
                            selectedCategoryId = expense.categoryId,
                            date = expense.date,
                            isIncome = expense.isIncome,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _formState.update { it.copy(isLoading = false, error = "Gagal memuat data") }
            }
        }
    }
    
    fun updateAmount(amount: String) {
        val filtered = amount.filter { it.isDigit() }
        _formState.update { it.copy(amount = filtered, error = null) }
    }
    
    fun updateName(name: String) {
        _formState.update { it.copy(name = name, error = null) }
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
    
    fun setTransactionType(isIncome: Boolean) {
        _formState.update { it.copy(isIncome = isIncome, selectedCategoryId = null, error = null) }
    }
    
    fun saveExpense() {
        val state = _formState.value
        

        when {
            state.amount.isBlank() || state.amount.toDoubleOrNull() == null || state.amount.toDouble() <= 0 -> {
                _formState.update { it.copy(error = "Masukkan jumlah yang valid") }
                return
            }
            state.name.isBlank() -> {
                _formState.update { it.copy(error = "Masukkan nama transaksi") }
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
                    id = state.editingId ?: 0,
                    amount = state.amount.toDouble(),
                    name = state.name,
                    description = state.description, // Opsional, bisa kosong
                    categoryId = state.selectedCategoryId!!,
                    date = state.date,
                    isIncome = state.isIncome,
                    createdAt = System.currentTimeMillis()
                )
                
                if (state.editingId != null) {
                    repository.updateExpense(expense)
                } else {
                    repository.insertExpense(expense)
                }
                _formState.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _formState.update { 
                    it.copy(isLoading = false, error = e.message ?: "Gagal menyimpan") 
                }
            }
        }
    }
    
    fun deleteExpense() {
        val editingId = _formState.value.editingId ?: return
        
        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true) }
            try {
                repository.deleteExpenseById(editingId)
                _formState.update { it.copy(isLoading = false, isDeleted = true) }
            } catch (e: Exception) {
                _formState.update { 
                    it.copy(isLoading = false, error = e.message ?: "Gagal menghapus") 
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

