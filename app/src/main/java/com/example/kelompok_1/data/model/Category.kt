package com.example.kelompok_1.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String, // Material icon name (e.g., "Restaurant", "DirectionsCar")
    val color: Long // Color as ARGB Long
)

// Predefined default categories
object DefaultCategories {
    val list = listOf(
        Category(id = 1, name = "Makanan & Minuman", icon = "Restaurant", color = 0xFFFF9800),
        Category(id = 2, name = "Transportasi", icon = "DirectionsCar", color = 0xFF2196F3),
        Category(id = 3, name = "Belanja", icon = "ShoppingCart", color = 0xFFE91E63),
        Category(id = 4, name = "Tagihan", icon = "Receipt", color = 0xFFFFC107),
        Category(id = 5, name = "Hiburan", icon = "Movie", color = 0xFF9C27B0),
        Category(id = 6, name = "Kesehatan", icon = "LocalHospital", color = 0xFFF44336),
        Category(id = 7, name = "Pendidikan", icon = "School", color = 0xFF009688),
        Category(id = 8, name = "Lainnya", icon = "MoreHoriz", color = 0xFF607D8B)
    )
}
