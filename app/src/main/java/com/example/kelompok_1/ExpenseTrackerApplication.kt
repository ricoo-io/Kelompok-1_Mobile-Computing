package com.example.kelompok_1

import android.app.Application
import com.example.kelompok_1.data.database.ExpenseDatabase
import com.example.kelompok_1.data.repository.ExpenseRepository
import com.example.kelompok_1.ui.theme.ThemePreferences

class ExpenseTrackerApplication : Application() {
    
    val database by lazy { ExpenseDatabase.getDatabase(this) }
    
    val repository by lazy {
        ExpenseRepository(
            database.expenseDao(),
            database.categoryDao()
        )
    }
    
    override fun onCreate() {
        super.onCreate()
        // Apply saved theme preference on app startup
        ThemePreferences.applyTheme(this)
    }
}
