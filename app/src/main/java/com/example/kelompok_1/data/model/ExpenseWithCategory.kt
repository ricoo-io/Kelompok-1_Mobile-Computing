package com.example.kelompok_1.data.model


data class ExpenseWithCategory(
    val id: Long,
    val amount: Double,
    val name: String, // Nama transaksi
    val description: String, // Catatan (opsional)
    val categoryId: Long,
    val date: Long,
    val isIncome: Boolean = false,
    val createdAt: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: Long
)

data class CategorySpending(
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: Long,
    val totalAmount: Double
)

data class DailySpending(
    val date: Long,
    val totalAmount: Double
)
