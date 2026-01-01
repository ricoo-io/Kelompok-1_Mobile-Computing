package com.example.kelompok_1.data.dao

import androidx.room.*
import com.example.kelompok_1.data.model.CategorySpending
import com.example.kelompok_1.data.model.DailySpending
import com.example.kelompok_1.data.model.Expense
import com.example.kelompok_1.data.model.ExpenseWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    
    @Query("""
        SELECT e.id, e.amount, e.name, e.description, e.categoryId, e.date, e.isIncome, e.createdAt,
               c.name as categoryName, c.icon as categoryIcon, c.color as categoryColor
        FROM expenses e
        INNER JOIN categories c ON e.categoryId = c.id
        ORDER BY e.date DESC
    """)
    fun getAllExpensesWithCategory(): Flow<List<ExpenseWithCategory>>
    
    @Query("""
        SELECT e.id, e.amount, e.name, e.description, e.categoryId, e.date, e.isIncome, e.createdAt,
               c.name as categoryName, c.icon as categoryIcon, c.color as categoryColor
        FROM expenses e
        INNER JOIN categories c ON e.categoryId = c.id
        WHERE e.date BETWEEN :startDate AND :endDate
        ORDER BY e.date DESC
    """)
    fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<ExpenseWithCategory>>
    
    @Query("""
        SELECT e.id, e.amount, e.name, e.description, e.categoryId, e.date, e.isIncome, e.createdAt,
               c.name as categoryName, c.icon as categoryIcon, c.color as categoryColor
        FROM expenses e
        INNER JOIN categories c ON e.categoryId = c.id
        WHERE e.categoryId = :categoryId AND e.date BETWEEN :startDate AND :endDate
        ORDER BY e.date DESC
    """)
    fun getExpensesByCategory(categoryId: Long, startDate: Long, endDate: Long): Flow<List<ExpenseWithCategory>>
    
    @Query("""
        SELECT e.id, e.amount, e.name, e.description, e.categoryId, e.date, e.isIncome, e.createdAt,
               c.name as categoryName, c.icon as categoryIcon, c.color as categoryColor
        FROM expenses e
        INNER JOIN categories c ON e.categoryId = c.id
        ORDER BY e.date DESC
        LIMIT :limit
    """)
    fun getRecentExpenses(limit: Int): Flow<List<ExpenseWithCategory>>
    
    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    fun getTotalByDateRange(startDate: Long, endDate: Long): Flow<Double>
    
    @Query("""
        SELECT c.id as categoryId, c.name as categoryName, c.icon as categoryIcon, c.color as categoryColor,
               COALESCE(SUM(e.amount), 0) as totalAmount
        FROM categories c
        LEFT JOIN expenses e ON c.id = e.categoryId AND e.date BETWEEN :startDate AND :endDate AND e.isIncome = 0
        GROUP BY c.id
        HAVING totalAmount > 0
        ORDER BY totalAmount DESC
    """)
    fun getSpendingByCategory(startDate: Long, endDate: Long): Flow<List<CategorySpending>>
    
    @Query("""
        SELECT date / 86400000 * 86400000 as date, SUM(amount) as totalAmount
        FROM expenses
        WHERE date BETWEEN :startDate AND :endDate AND isIncome = 0
        GROUP BY date / 86400000
        ORDER BY date ASC
    """)
    fun getDailySpending(startDate: Long, endDate: Long): Flow<List<DailySpending>>
    
    // Get total income for date range
    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE date BETWEEN :startDate AND :endDate AND isIncome = 1")
    fun getTotalIncome(startDate: Long, endDate: Long): Flow<Double>
    
    // Get total expenses for date range
    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE date BETWEEN :startDate AND :endDate AND isIncome = 0")
    fun getTotalExpenses(startDate: Long, endDate: Long): Flow<Double>
    
    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getById(id: Long): Expense?
    
    @Insert
    suspend fun insert(expense: Expense): Long
    
    @Update
    suspend fun update(expense: Expense)
    
    @Delete
    suspend fun delete(expense: Expense)
    
    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteById(id: Long)
}
