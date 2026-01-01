package com.example.kelompok_1.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: Long,
    val type: String = "expense"
)


object DefaultCategories {
    val list = listOf(

        Category(id = 1, name = "Makanan & Minuman", icon = "Restaurant", color = 0xFFFF9800, type = "expense"),
        Category(id = 2, name = "Transportasi", icon = "DirectionsCar", color = 0xFF2196F3, type = "expense"),
        Category(id = 3, name = "Belanja", icon = "ShoppingCart", color = 0xFFE91E63, type = "expense"),
        Category(id = 4, name = "Tagihan", icon = "Receipt", color = 0xFFFFC107, type = "expense"),
        Category(id = 5, name = "Hiburan", icon = "Movie", color = 0xFF9C27B0, type = "expense"),
        Category(id = 6, name = "Kesehatan", icon = "LocalHospital", color = 0xFFF44336, type = "expense"),
        Category(id = 7, name = "Pendidikan", icon = "School", color = 0xFF009688, type = "expense"),
        Category(id = 8, name = "Lainnya", icon = "MoreHoriz", color = 0xFF607D8B, type = "expense"),

        Category(id = 9, name = "Gaji", icon = "AccountBalance", color = 0xFF4CAF50, type = "income"),
        Category(id = 10, name = "Bonus", icon = "CardGiftcard", color = 0xFF8BC34A, type = "income"),
        Category(id = 11, name = "Investasi", icon = "TrendingUp", color = 0xFF00BCD4, type = "income"),
        Category(id = 12, name = "Hadiah", icon = "Redeem", color = 0xFFFF5722, type = "income"),
        Category(id = 13, name = "Pemasukan Lainnya", icon = "AttachMoney", color = 0xFF795548, type = "income")
    )
}

