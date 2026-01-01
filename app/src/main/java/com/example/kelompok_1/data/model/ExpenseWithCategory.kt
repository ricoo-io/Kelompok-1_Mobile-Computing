package com.example.kelompok_1.data.model

/**
 * Data class for expense with category information joined
 */
data class ExpenseWithCategory(
    val id: Long,
    val amount: Double,
    val description: String,
    val categoryId: Long,
    val date: Long,
    val isIncome: Boolean = false,
    val createdAt: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: Long
)

/**
 * Data class for category spending summary
 */
data class CategorySpending(
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: Long,
    val totalAmount: Double
)

/**
 * Data class for daily spending trend
 */
data class DailySpending(
    val date: Long,
    val totalAmount: Double
)
