package com.example.kelompok_1.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object ThemePreferences {
    private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    
    fun isDarkMode(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[IS_DARK_MODE] ?: false
        }
    }
    
    suspend fun setDarkMode(context: Context, isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDarkMode
        }
    }
}
