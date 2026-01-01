package com.example.kelompok_1.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_DEFAULT
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val name: String, // Nama transaksi (wajib)
    val description: String = "", // Catatan (opsional)
    val categoryId: Long,
    val date: Long, // timestamp in milliseconds
    val isIncome: Boolean = false, // true = income, false = expense
    val createdAt: Long = System.currentTimeMillis()
)
