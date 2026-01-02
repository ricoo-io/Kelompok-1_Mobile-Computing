package com.example.kelompok_1

import android.app.Application
import com.example.kelompok_1.data.database.ExpenseDatabase
import com.example.kelompok_1.data.repository.ExpenseRepository

class ExpenseTrackerApplication : Application() {
    
    val database by lazy { ExpenseDatabase.getDatabase(this) }
    
    val repository by lazy {
        ExpenseRepository(
            database.expenseDao(),
            database.categoryDao()
        )
    }
}
