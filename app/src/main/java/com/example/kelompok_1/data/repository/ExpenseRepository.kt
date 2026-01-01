package com.example.kelompok_1.data.repository

import com.example.kelompok_1.data.dao.BudgetDao
import com.example.kelompok_1.data.dao.CategoryDao
import com.example.kelompok_1.data.dao.ExpenseDao
import com.example.kelompok_1.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.*

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao,
    private val budgetDao: BudgetDao
) {

    fun getAllExpenses(): Flow<List<ExpenseWithCategory>> = expenseDao.getAllExpensesWithCategory()
    
    fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<ExpenseWithCategory>> =
        expenseDao.getExpensesByDateRange(startDate, endDate)
    
    fun getExpensesByCategory(categoryId: Long, startDate: Long, endDate: Long): Flow<List<ExpenseWithCategory>> =
        expenseDao.getExpensesByCategory(categoryId, startDate, endDate)
    
    fun getRecentExpenses(limit: Int = 5): Flow<List<ExpenseWithCategory>> =
        expenseDao.getRecentExpenses(limit)
    
    fun getTotalByDateRange(startDate: Long, endDate: Long): Flow<Double> =
        expenseDao.getTotalByDateRange(startDate, endDate)
    
    fun getSpendingByCategory(startDate: Long, endDate: Long): Flow<List<CategorySpending>> =
        expenseDao.getSpendingByCategory(startDate, endDate)
    
    fun getDailySpending(startDate: Long, endDate: Long): Flow<List<DailySpending>> =
        expenseDao.getDailySpending(startDate, endDate)
    
    fun getTotalIncome(startDate: Long, endDate: Long): Flow<Double> =
        expenseDao.getTotalIncome(startDate, endDate)
    
    fun getTotalExpenses(startDate: Long, endDate: Long): Flow<Double> =
        expenseDao.getTotalExpenses(startDate, endDate)
    
    suspend fun insertExpense(expense: Expense): Long = expenseDao.insert(expense)
    
    suspend fun updateExpense(expense: Expense) = expenseDao.update(expense)
    
    suspend fun deleteExpense(expense: Expense) = expenseDao.delete(expense)
    
    suspend fun deleteExpenseById(id: Long) = expenseDao.deleteById(id)
    
    suspend fun getExpenseById(id: Long): Expense? = expenseDao.getById(id)

    
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
    
    fun getCategoriesByType(type: String): Flow<List<Category>> = categoryDao.getCategoriesByType(type)
    
    suspend fun getCategoryById(id: Long): Category? = categoryDao.getCategoryById(id)
    
    suspend fun insertCategory(category: Category): Long = categoryDao.insert(category)
    
    suspend fun updateCategory(category: Category) = categoryDao.update(category)
    
    suspend fun deleteCategory(category: Category) = categoryDao.delete(category)
    
    suspend fun deleteCategoryById(id: Long) = categoryDao.deleteById(id)

    
    fun getBudgetsByMonth(month: Int, year: Int): Flow<List<Budget>> =
        budgetDao.getBudgetsByMonth(month, year)
    
    suspend fun getBudget(categoryId: Long, month: Int, year: Int): Budget? =
        budgetDao.getBudget(categoryId, month, year)
    
    fun getTotalBudget(month: Int, year: Int): Flow<Double> =
        budgetDao.getTotalBudget(month, year)
    
    suspend fun insertBudget(budget: Budget): Long = budgetDao.insert(budget)
    
    suspend fun updateBudget(budget: Budget) = budgetDao.update(budget)
    
    suspend fun deleteBudget(budget: Budget) = budgetDao.delete(budget)

    
    companion object {
        fun getStartOfDay(date: Long = System.currentTimeMillis()): Long {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return calendar.timeInMillis
        }
        
        fun getEndOfDay(date: Long = System.currentTimeMillis()): Long {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = date
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            return calendar.timeInMillis
        }
        
        fun getStartOfWeek(): Long {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return calendar.timeInMillis
        }
        
        fun getStartOfMonth(): Long {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return calendar.timeInMillis
        }
        
        fun getStartOfYear(): Long {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return calendar.timeInMillis
        }
        
        fun getDaysAgo(days: Int): Long {
            val calendar = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -days)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return calendar.timeInMillis
        }
    }
}
