package com.example.kelompok_1.data.dao

import androidx.room.*
import com.example.kelompok_1.data.model.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    
    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year")
    fun getBudgetsByMonth(month: Int, year: Int): Flow<List<Budget>>
    
    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId AND month = :month AND year = :year")
    suspend fun getBudget(categoryId: Long, month: Int, year: Int): Budget?
    
    @Query("SELECT COALESCE(SUM(amount), 0) FROM budgets WHERE month = :month AND year = :year")
    fun getTotalBudget(month: Int, year: Int): Flow<Double>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget): Long
    
    @Update
    suspend fun update(budget: Budget)
    
    @Delete
    suspend fun delete(budget: Budget)
    
    @Query("DELETE FROM budgets WHERE categoryId = :categoryId AND month = :month AND year = :year")
    suspend fun deleteByCategoryAndMonth(categoryId: Long, month: Int, year: Int)
}
