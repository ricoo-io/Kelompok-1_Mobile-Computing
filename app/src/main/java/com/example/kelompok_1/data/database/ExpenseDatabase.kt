package com.example.kelompok_1.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kelompok_1.data.dao.CategoryDao
import com.example.kelompok_1.data.dao.ExpenseDao
import com.example.kelompok_1.data.model.Category
import com.example.kelompok_1.data.model.DefaultCategories
import com.example.kelompok_1.data.model.Expense
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Expense::class, Category::class],
    version = 4,
    exportSchema = false
)
abstract class ExpenseDatabase : RoomDatabase() {
    
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    
    companion object {
        @Volatile
        private var INSTANCE: ExpenseDatabase? = null
        
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE expenses ADD COLUMN isIncome INTEGER NOT NULL DEFAULT 0")
                
                db.execSQL("ALTER TABLE categories ADD COLUMN type TEXT NOT NULL DEFAULT 'expense'")

                db.execSQL("INSERT INTO categories (id, name, icon, color, type) VALUES (9, 'Gaji', 'AccountBalance', ${0xFF4CAF50}, 'income')")
                db.execSQL("INSERT INTO categories (id, name, icon, color, type) VALUES (10, 'Bonus', 'CardGiftcard', ${0xFF8BC34A}, 'income')")
                db.execSQL("INSERT INTO categories (id, name, icon, color, type) VALUES (11, 'Investasi', 'TrendingUp', ${0xFF00BCD4}, 'income')")
                db.execSQL("INSERT INTO categories (id, name, icon, color, type) VALUES (12, 'Hadiah', 'Redeem', ${0xFFFF5722}, 'income')")
                db.execSQL("INSERT INTO categories (id, name, icon, color, type) VALUES (13, 'Pemasukan Lainnya', 'AttachMoney', ${0xFF795548}, 'income')")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE expenses ADD COLUMN name TEXT NOT NULL DEFAULT ''")
                db.execSQL("UPDATE expenses SET name = description")
            }
        }
        
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS budgets")
            }
        }
        
        fun getDatabase(context: Context): ExpenseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseDatabase::class.java,
                    "expense_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    database.categoryDao().insertAll(DefaultCategories.list)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
