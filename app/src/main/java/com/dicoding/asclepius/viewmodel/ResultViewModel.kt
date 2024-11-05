package com.dicoding.asclepius.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.database.AppDatabase
import com.dicoding.asclepius.data.model.ScanHistory
import kotlinx.coroutines.launch

class ResultViewModel(application: Application) : AndroidViewModel(application) {

    fun saveToHistory(history: ScanHistory) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(getApplication())
            db.scanHistoryDao().insert(history)
        }
    }
}